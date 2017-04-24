package com.neo_lab.demotwilio;

import android.app.Application;

import com.neo_lab.demotwilio.di.components.NetComponent;

/**
 * Created by sam_nguyen on 11/04/2017.
 */

public class DemoTwilioApplication extends Application {

    private NetComponent netComponent;

    @Override
    public void onCreate() {
        super.onCreate();


    }
}
