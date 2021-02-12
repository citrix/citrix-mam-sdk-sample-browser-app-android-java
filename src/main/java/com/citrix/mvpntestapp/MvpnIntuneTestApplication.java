package com.citrix.mvpntestapp;

import android.os.StrictMode;

import com.microsoft.intune.mam.client.app.MAMApplication;


public class MvpnIntuneTestApplication extends MAMApplication {
    private static final String TAG = "MvpnIntuneTestApplication";

    @Override
    public void onMAMCreate() {
        super.onMAMCreate();

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
