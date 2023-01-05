/*
 * Copyright (c) 2023. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.citrix.mvpntestapp.activities;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.citrix.mvpn.api.MicroVPNSDK;
import com.citrix.mvpn.exception.MvpnException;
import com.citrix.mvpntestapp.R;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.citrix.mvpntestapp.activities.MainActivity.URL_KEY;

public class OkHttpActivity extends AppCompatActivity {
    private static final String TAG = "OkHttpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_okhttp);

        new OkHttpAsyncTask(this).execute(getIntent().getStringExtra(URL_KEY));
    }

    private void displayResponse(String responseHtml) {
        TextView textView = findViewById(R.id.okHttpTextViewId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(responseHtml, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(responseHtml));
        }
    }

    private static class OkHttpAsyncTask extends AsyncTask<String, Void, String> {
        private static final String LOG_TAG = "MVPN-OkHttpAsyncTask";

        private WeakReference<OkHttpActivity> reference;

        private OkHttpAsyncTask(OkHttpActivity activity) {
            reference = new WeakReference<>(activity);
        }

        private OkHttpActivity getActivity() {
            if (reference != null) {
                return reference.get();
            }

            return null;
        }

        @Override
        protected String doInBackground(String... strUrl) {
            OkHttpActivity activity = getActivity();
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(strUrl[0]).build();

            try {
                client = (OkHttpClient) MicroVPNSDK.enableOkHttpClientObjectForNetworkTunnel(activity, client);
            } catch (MvpnException e) {
                Log.e(TAG, e.getMessage());
            }

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return "";
        }

        @Override
        protected void onPostExecute(String responseHtml) {
            OkHttpActivity activity = getActivity();

            if (activity != null && !activity.isFinishing()) {
                activity.displayResponse(responseHtml);
            }
        }
    }
}
