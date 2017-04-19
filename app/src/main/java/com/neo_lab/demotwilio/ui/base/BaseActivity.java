package com.neo_lab.demotwilio.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by sam_nguyen on 19/04/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public void onUnknownError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onTimeOut(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onTimeOut(int message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onNetworkError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onNetworkError(int message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
