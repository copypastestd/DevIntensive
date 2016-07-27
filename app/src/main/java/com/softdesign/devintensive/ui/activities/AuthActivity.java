package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.manager.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Respository;
import com.softdesign.devintensive.data.storage.models.RespositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A login screen activity.
 */
public class AuthActivity extends BaseActivity {

    private DataManager mDataManager;

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.login_btn)
    Button mSignIn;
    @BindView(R.id.remember_txt)
    TextView mRememberPassword;
    @BindView(R.id.login_email_et)
    EditText mLogin;
    @BindView(R.id.login_password_et)
    EditText mPassword;

    private RespositoryDao mRespositoryDao;
    private UserDao mUserDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRespositoryDao = mDataManager.getDaoSession().getRespositoryDao();

        /** Для тестирования */
  /*      mLogin.setText("copypastestd@gmail.com");
        mPassword.setText("123456");*/
        //mLogin.setText("shmakova-nastya@yandex.ru");
        //mPassword.setText("iliich");
    }


    @OnClick(R.id.login_btn)
    public void onLoginBtnClick() {
        signIn();
    }

    @OnClick(R.id.remember_txt)
    public void onRememberPasswordClick() {
        rememberPassword();
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSuccess(UserModelRes userModel) {

        showSnackbar(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        //saveUserData(userModel);
        saveUserValues(userModel);
        saveUserInDb();

        Intent loginIntent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(loginIntent);

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        }, AppConfig.START_DELAY);*/

    }

    private void signIn() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
            // TODO: FIX  Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq("copypastestd@gmail.com", "123456"));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body());
                    } else if (response.code() == 404) {
                        showSnackbar("Неверный логин или пароль");
                    } else {
                        showSnackbar("Все пропало Шеф!!!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    // TODO: 12.07.2016 обработать ошибки ретрофита
                }
            });
        } else {
            showSnackbar("Сеть на данный момент не доступна, попробуйте позже");
        }
    }

    private void saveUserValues(UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects(),
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);

        List<String> userData = new ArrayList<>();
        userData.add(userModel.getData().getUser().getContacts().getPhone());
        userData.add(userModel.getData().getUser().getContacts().getEmail());
        userData.add(userModel.getData().getUser().getContacts().getVk());
        userData.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        userData.add(userModel.getData().getUser().getPublicInfo().getBio());
        mDataManager.getPreferencesManager().saveUserProfileData(userData);

        String userName = userModel.getData().getUser().getFirstName()
                + " " + userModel.getData().getUser().getSecondName();
        mDataManager.getPreferencesManager().saveUserName(userName);

        String avatarUrl = userModel.getData().getUser().getPublicInfo().getAvatar();
        mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(avatarUrl));

        String photoUrl = userModel.getData().getUser().getPublicInfo().getPhoto();
        mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(photoUrl));
    }

    private void saveUserInDb() {
        Call<UserListRes> call = mDataManager.getUserListFromNetwork();

        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                try {
                    if (response.code() == 200) {
                        List<Respository> allRespositories = new ArrayList<Respository>();
                        List<User> allUsers = new ArrayList<User>();

                        for (UserListRes.UserData userRes : response.body().getData()) {

                            allRespositories.addAll(getRepoListFromUserRes(userRes));
                            allUsers.add(new User(userRes));

                            mRespositoryDao.insertOrReplaceInTx(allRespositories);
                            mUserDao.insertOrReplaceInTx(allUsers);

                        }
                    } else {
                        showSnackbar("Список пользователей не может быть получен");
                        Log.e(TAG, "onResponse: " + String.valueOf(response.errorBody().source()));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    showSnackbar("Что то пошло не так");
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                //TODO Обработать ошибки
            }
        });
    }

    private List<Respository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();

        List<Respository> respositories = new ArrayList<>();
        for (UserModelRes.Repo respositoryRes : userData.getRepositories().getRepo()) {
            respositories.add(new Respository(respositoryRes, userId));
        }

        return respositories;

    }


}

