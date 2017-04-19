package com.neo_lab.demotwilio.ui.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.neo_lab.demotwilio.R;
import com.neo_lab.demotwilio.ui.video_calling_room.VideoCallingRoomActivity;

/**
 * Created by sam_nguyen on 19/04/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_CODE = 69;

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


    public boolean isSpecificPermissionsGranted(String[] permissions) {

        int size = permissions.length;

        for (int index = 0; index < size; index ++) {
            int result = ContextCompat.checkSelfPermission(this, permissions[index]);

            if (result != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;

    }

    public void requestSpecificPermissions(String[] permissions, int message) {

        int size = permissions.length;

        boolean isshouldShowRequestPermissionRationale = false;

        for (int index = 0; index < size; index ++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[index])) {
                isshouldShowRequestPermissionRationale = true;
                break;
            }
        }

        if (isshouldShowRequestPermissionRationale) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        }

    }

    public boolean checkRequestPermissionsResult(int[] grantResults) {
        boolean permissionGranted = true;

        for (int grantResult : grantResults) {
            permissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
        }

        return permissionGranted;
    }

}
