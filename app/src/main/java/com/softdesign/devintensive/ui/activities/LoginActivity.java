package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.softdesign.devintensive.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen activity.
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.sign_in_btn)
    Button mSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.sign_in_btn)
    public void OnSignInBtnClick() {
        startActivity(new Intent(this, MainActivity.class));
    }
}

