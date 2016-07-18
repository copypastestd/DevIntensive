package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.manager.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedTransformation;
import com.softdesign.devintensive.utils.UserDataTextWatcher;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;

    private int mCurrentEditMode = 0;

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.call_img)
    ImageView mCallImg;
    @BindView(R.id.send_email_img)
    ImageView mSendEmailImg;
    @BindView(R.id.watch_vk_img)
    ImageView mWatchVkImg;
    @BindView(R.id.watch_github_img)
    ImageView mWatchGithubImg;

    @BindView(R.id.user_info_rate_txt)
    TextView mUserValuesRating;
    @BindView(R.id.user_info_code_line_txt)
    TextView mUserValuesCodeLines;
    @BindView(R.id.user_info_project_txt)
    TextView mUserValuesProjects;

    @BindView(R.id.phone_layout)
    LinearLayout phoneLayout;
    @BindView(R.id.email_layout)
    LinearLayout emailLayout;
    @BindView(R.id.vk_layout)
    LinearLayout vkLayout;
    @BindView(R.id.github_layout)
    LinearLayout githubLayout;

    @BindView(R.id.input_layout_phone)
    TextInputLayout inputLayoutPhone;
    @BindView(R.id.input_layout_email)
    TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_layout_vk)
    TextInputLayout inputLayoutVk;
    @BindView(R.id.input_layout_github)
    TextInputLayout inputLayoutGithub;

    @BindView(R.id.phone_et)
    EditText mUserPhone;
    @BindView(R.id.email_et)
    EditText mUserMail;
    @BindView(R.id.vk_et)
    EditText mUserVk;
    @BindView(R.id.github_et)
    EditText mUserGit;
    @BindView(R.id.bio_et)
    EditText mUserBio;

    private List<TextView> mUserValuesViews;
    private List<EditText> mUserInfoViews;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        mUserValuesViews = new ArrayList<>();
        mUserValuesViews.add(mUserValuesRating);
        mUserValuesViews.add(mUserValuesCodeLines);
        mUserValuesViews.add(mUserValuesProjects);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        setupToolbar();
        setupDrawer();
        //fillDataToEditTexts();
        initUserFields(); //saveUserFields();
        initUserInfoValue();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_bg) // TODO: 03.07.2016 сделать плейсхолдер и transform + crop
                .into(mProfileImage);

        if (savedInstanceState == null) {
            // активити запускается впервые

        } else {
            // активити уже создавалось
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
            // initUserFields();
            //initUserFields();

        }

        mUserPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mUserPhone.addTextChangedListener(new UserDataTextWatcher(this, phoneLayout, inputLayoutPhone, mUserPhone));
        mUserMail.addTextChangedListener(new UserDataTextWatcher(this, emailLayout, inputLayoutEmail, mUserMail));
        mUserVk.addTextChangedListener(new UserDataTextWatcher(this, vkLayout, inputLayoutVk, mUserVk));
        mUserGit.addTextChangedListener(new UserDataTextWatcher(this, githubLayout, inputLayoutGithub, mUserGit));

}

    /**
     * @deprecated
     * Заполняет поля значениями по умолчанию,
     * при первом запуске приложения
     */
    private void fillDataToEditTexts() {
        mUserPhone.setText(getString(R.string.phone_et_text));
        mUserMail.setText(getString(R.string.email_et_text));
        mUserVk.setText(getString(R.string.vk_et_text));
        mUserGit.setText(getString(R.string.github_et_text));
        mUserBio.setText(getString(R.string.bio_et_text));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserFields();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        if (mCurrentEditMode == 0) {
            changeEditMode(1);
            mCurrentEditMode = 1;
            mUserPhone.requestFocus();
        } else {
            changeEditMode(0);
            mCurrentEditMode = 0;
        }
    }

    @OnClick(R.id.profile_placeholder)
    public void onProfilePlaceholderClick() {
        // TODO: 01.07.2016 сделать выбор откуда загружать фото
        showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
    }

    @OnClick(R.id.call_img)
    public void onCallClick() {
        makeCall();
    }

    @OnClick(R.id.send_email_img)
    public void onSendEmailClick() {
        sendEmail();
    }

    @OnClick(R.id.watch_vk_img)
    public void onWatchVkClick() {
        watchVk();
    }

    @OnClick(R.id.watch_github_img)
    public void onWatchGithubClick() {
        watchGithub();
    }

    /**
     * закрывает Drawer по нажатию кнопки Back
     */
    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        mCollapsingToolbar.setTitle(DataManager.getInstance().getPreferencesManager().getUserName());
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        //NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View hView = navigationView.getHeaderView(0);
        ImageView mAvatar = (ImageView) hView.findViewById(R.id.avatar);
        TextView drawerUserName = (TextView) hView.findViewById(R.id.user_name_txt);
        TextView drawerEmail = (TextView) hView.findViewById(R.id.user_email_txt);

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserAvatar())
                .placeholder(R.drawable.nav_header_bg)
                .transform(new RoundedTransformation())
                //.transform(RoundedAvatarDrawable.getRoundedBitmap(BitmapFactory.decodeResource(getResources(), Integer.parseInt(DataManager.getInstance().getPreferencesManager().loadUserAvatar().toString())
                //.transform(RoundedTransformation)
                .into(mAvatar);

        drawerUserName.setText(DataManager.getInstance().getPreferencesManager().getUserName());
        drawerEmail.setText(DataManager.getInstance().getPreferencesManager().getUserEmail());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.team_menu:
                        startActivity(new Intent(MainActivity.this, UserListActivity.class));

                        item.setChecked(true);
                        break;
                }
                showSnackbar(item.getTitle().toString());
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                return false;
            }
        });

    }

    /**
     * Получение результата из другой Activity (фото из камеры или галлереи)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }

    /**
     * переключает режим редактирования
     *
     * @param mode, если 1 редактирования, если 0 режим просмотра
     */
    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));

                saveUserFields();
            }
        }
    }

    /**
     * @deprecated
     * Загружает аватар пользователя
     */
    private void initUserAvatar(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserAvatar(selectedImage);
    }


    /**
     * Загружает данные пользователя
     */
    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохраняет данные пользователя
     */
    private void saveUserFields() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldsView : mUserInfoViews) {
            userData.add(userFieldsView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * Загружает статистику пользователя
     */
    private void initUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserValuesViews.get(i).setText(userData.get(i));
        }

    }

    private void loadPhotoFromGallery() {

        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_choice_message)), ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    private void loadPhotoFromCamera() {
        Log.d("MY_TAG", "HERE");
        //if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        //&& ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            //if(true) {
            Log.d("MY_TAG", "CHECK PERMISSIONS");

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                Log.d("MY_TAG", "TRY");
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 02.07.2016 обработать ошибку
            }

            if (mPhotoFile != null) {
                // TODO: 02.07.2016 передать фотофайл в интент
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takePictureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, "Для корректной работы приложения необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: 03.07.2016 тут обрабатываем разрешение (разрешение получено)
                // например вывести сообщение или обработать какой то логикой если нужно
            }
        }

        if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // TODO: 03.07.2016 тут обрабатываем разрешение (разрешение получено)
            // например вывести сообщение или обработать какой то логикой если нужно
        }
    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.user_profile_dialog_title);
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                // TODO: 01.07.2016 загрузить из галлереи
                                loadPhotoFromGallery();
                                //showSnackbar("загрузить из галлереи");
                                break;
                            case 1:
                                // TODO: 01.07.2016 загрузить из камеры
                                loadPhotoFromCamera();
                                //showSnackbar("загрузить из камеры");
                                break;
                            case 2:
                                // TODO: 01.07.2016 отмена
                                dialog.cancel();
                                showSnackbar("отмена");
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, "jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        return image;
    }

    /**
     * Вставляет изображение профиля
     * @param //selectedImage
     */
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);
        // TODO: 03.07.2016 сделать плейсхолдер и transform + crop
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    /**
     * Совершает звонок по номеру указанному в профиле
     */
    private void makeCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            String telNumber;
            if(!mUserPhone.getText().toString().equals("")) {
                telNumber = String.valueOf(mUserPhone.getText()).trim();
                final Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNumber));
                startActivity(callIntent);
            } else {
                showSnackbar(getString(R.string.warning_phone_number));
            }
        }
    }

    /**
     * Отправляет email по адресу указанному в профиле
     */
    private void sendEmail() {
        String emailAddress;
        if (!mUserMail.getText().toString().equals("")) {
            emailAddress = String.valueOf(mUserMail.getText()).trim();
            final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: " + emailAddress));
            startActivity(emailIntent);
        } else {
            showSnackbar(getString(R.string.warning_email_address));
        }

    }

    /**
     * Открывает профиль Vk по адресу указанному в профиле
     */
    private void watchVk() {
        String vkAddress;
        if (!mUserVk.getText().toString().equals("")) {
            vkAddress = String.valueOf(mUserVk.getText()).trim();
            Intent watchVkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.vk.com/" + vkAddress));
            startActivity(watchVkIntent);
        } else {
            showSnackbar(getString(R.string.warning_vk_profile));
        }
    }

    /**
     * Открывает профиль GitHub по адресу указанному в профиле
     */
    private void watchGithub() {
        String githubAddress;
        if (!mUserGit.getText().toString().equals("")) {
            githubAddress = String.valueOf(mUserGit.getText()).trim();
            Intent watchGithubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/" + githubAddress));
            startActivity(watchGithubIntent);
        } else {
            showSnackbar(getString(R.string.warning_github_profile));
        }
    }


}
