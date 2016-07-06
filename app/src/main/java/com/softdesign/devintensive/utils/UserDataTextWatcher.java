package com.softdesign.devintensive.utils;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.softdesign.devintensive.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qwsa on 06.07.2016.
 */
public class UserDataTextWatcher implements TextWatcher {

    private Activity mActivity;
    private LinearLayout mLayout;
    private EditText mEditText;
    private TextInputLayout mInputLayout;

    public UserDataTextWatcher(Activity activity, LinearLayout layout, TextInputLayout inputLayout, EditText editText) {
        this.mActivity = activity;
        this.mLayout = layout;
        this.mEditText = editText;
        this.mInputLayout = inputLayout;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void afterTextChanged(Editable editable) {
        switch (mEditText.getId()) {
            case R.id.phone_et:
                validatePhone(editable);
                break;
            case R.id.email_et:
                validateEmail(editable);
                break;
            case R.id.vk_et:
                validateVk(editable);
                break;
            case R.id.github_et:
                validateGithub(editable);
                break;
        }
    }

    private boolean validatePhone(Editable editable) {
        String phone = editable.toString().trim();
        if (phone.isEmpty() || phone.length() < 11 || phone.length() > 20) {
            String error = mActivity.getResources().getString(R.string.err_msg_phone);
            mInputLayout.setError(error);
            requestFocus(mEditText);
            expandLayoutForShowingError();
            return false;
        } else {
            mInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail(Editable editable) {
        String email = editable.toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            String error = mActivity.getResources().getString(R.string.err_msg_email);
            mInputLayout.setError(error);
            requestFocus(mEditText);
            expandLayoutForShowingError();
            return false;
        } else {
            mInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateVk(Editable editable) {
        String vk = editable.toString().trim();

        if (vk.isEmpty() || !isValidVk(vk)) {
            String error = mActivity.getResources().getString(R.string.err_msg_vk);
            mInputLayout.setError(error);
            requestFocus(mEditText);
            expandLayoutForShowingError();
            return false;
        } else {
            mInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateGithub(Editable editable) {
        String github = editable.toString().trim();

        if (github.isEmpty() || !isValidGithub(github)) {
            String error = mActivity.getResources().getString(R.string.err_msg_github);
            mInputLayout.setError(error);
            requestFocus(mEditText);
            expandLayoutForShowingError();
            return false;
        } else {
            mInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * Увеличивет Layout для корректного отображения ошибки
     */
    private void expandLayoutForShowingError() {
        final LinearLayout.LayoutParams lp =
                (LinearLayout.LayoutParams) mLayout.getLayoutParams();
        lp.height = mActivity.getResources().getDimensionPixelSize(R.dimen.size_xlarge_88);
        mLayout.setLayoutParams(lp);
    }

    private static boolean isValidEmail(String email) {
        //String EMALI_REGIX = "^[\\\\w!#$%&’*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$";
        //String EMAIL_REGIX = "^[A-Za-z0-9+_.-]{2}+@(^[A-Za-z0-9+_.-]{2}+\\.)+[a-zA-Z]{2}$";
        String EMAIL_REGIX = "^[A-Za-z0-9+_.-]{3,}+@([A-Za-z0-9+_.-]{2,})+\\.+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(EMAIL_REGIX);
        Matcher matcher = pattern.matcher(email);
        return ((!email.isEmpty()) && (matcher.matches()));
    }

    private static boolean isValidVk(String vk) {
        String VK_REGIX = "^vk\\.com\\/[\\w]{3,}+$";
        Pattern pattern = Pattern.compile(VK_REGIX);
        Matcher matcher = pattern.matcher(vk);
        return ((!vk.isEmpty()) && (matcher.matches()));
    }

    private static boolean isValidGithub(String github) {
        String GITHUB_REGIX = "^github\\.com\\/[\\w]{3,}+$";
        Pattern pattern = Pattern.compile(GITHUB_REGIX);
        Matcher matcher = pattern.matcher(github);
        return ((!github.isEmpty()) && (matcher.matches()));
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
