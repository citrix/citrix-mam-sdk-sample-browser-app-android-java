/*
 * Copyright (c) 2023. Cloud Software Group, Inc.
 */

package com.citrix.mvpntestapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.citrix.mvpn.api.MicroVPNSDK;
import com.citrix.mvpntestapp.R;
import com.citrix.mvpntestapp.util.UrlUtil;

import static com.citrix.mvpntestapp.activities.MainActivity.URL_KEY;

public class SelectConnectionActivity extends AppCompatActivity {
    private EditText uriText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_start_tunnel);

        if (!MicroVPNSDK.isNetworkTunnelRunning(this)) {
            Toast.makeText(this, R.string.start_tunnel_failed_message, Toast.LENGTH_LONG).show();
        }

        uriText = findViewById(R.id.inputURL);
        uriText.setText(UrlUtil.getSavedUrl(this));
        uriText.setSelection(uriText.getText().length());
    }

    public void onWebViewClicked(View view) {
        startActivity(createIntentWithUrl(WebViewActivity.class));
    }

    public void onOkHttpClicked(View view) {
        startActivity(createIntentWithUrl(OkHttpActivity.class));
    }

    public void onURLConnectionClicked(View view) {
        startActivity(createIntentWithUrl(UrlConnectionActivity.class));
    }

    private Intent createIntentWithUrl(Class<?> cls) {
        Intent intent = new Intent(this, cls);

        if (uriText.getText() != null && !TextUtils.isEmpty(uriText.getText().toString())) {
            String url = uriText.getText().toString();
            intent.putExtra(URL_KEY, url);
            UrlUtil.saveUrl(this, url);
        } else {
            intent.putExtra(URL_KEY, getString(R.string.test_uri));
        }

        return intent;
    }
}
