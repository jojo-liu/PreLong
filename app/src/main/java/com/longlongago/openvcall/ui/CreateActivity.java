package com.longlongago.openvcall.ui;

import android.content.Intent;
import android.os.Bundle;

import com.longlongago.openvcall.LLAApplication;
import com.longlongago.resideMenu.MenuActivity;
import com.longlongago.userInterface.RoomActivity;

public class CreateActivity extends BaseActivity {

    private static final String TAG = "CreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //add current activity into the container of LLAApplication
        LLAApplication.getInstance().addActivity(this);
//        setContentView(R.layout.home);
        Intent i = new Intent(CreateActivity.this, RoomActivity.class);
        startActivity(i);
    }

    @Override
    protected void initUIandEvent() {

    }

    @Override
    protected void deInitUIandEvent() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(CreateActivity.this, MenuActivity.class);
        startActivity(i);
    }

//    public void onClickSetting(View view) {
//        Intent i = new Intent(this, SettingsActivity.class);
//    }

//    public void onClickRegister(View view) {
//        Intent i = new Intent(this, RegisterActivity.class);
//        startActivity(i);
//    }
//
//    public void onLogin(View view) {
//        Intent i = new Intent(this, LoginActivity.class);
//        startActivity(i);
//    }
}
