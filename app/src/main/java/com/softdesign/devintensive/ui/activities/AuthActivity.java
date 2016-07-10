package com.softdesign.devintensive.ui.activities;

import android.os.Bundle;

import com.softdesign.devintensive.R;

import butterknife.ButterKnife;

/**
 * A login screen activity.
 */
public class AuthActivity extends BaseActivity {

    /*@BindView(R.id.sign_in_btn)
    Button mSignInBtn;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
    }

    /*@OnClick(R.id.sign_in_btn)
    public void OnSignInBtnClick() {
        startActivity(new Intent(this, MainActivity.class));
    }*/
}

