/*
 * Copyright (c) 2023. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.citrix.mvpntestapp;

import android.app.Application;
import android.os.StrictMode;


public class MvpnCemTestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectNetwork()   // detects N/W access on UI thread.
                    .permitDiskReads()
                    .permitDiskWrites()
                    .penaltyLog()
                    .penaltyDialog()
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().build());
        }
    }
}
