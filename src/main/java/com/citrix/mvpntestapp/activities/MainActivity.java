package com.citrix.mvpntestapp.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.citrix.mvpntestapp.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mam_provider);
    }

    public void onSecureHubClicked(View view) {
        Intent intent = new Intent(this, SelectStartTunnelActivity.class);
        startActivityForResult(intent, 0);
    }

    public void onCompanyPortalClicked(View view) {
        Intent intent = new Intent(this, IntuneMAMEnrollmentActivity.class);
        startActivityForResult(intent, 0);
    }
}
