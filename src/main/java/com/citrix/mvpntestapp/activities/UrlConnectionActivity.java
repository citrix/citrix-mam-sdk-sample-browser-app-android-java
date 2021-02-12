package com.citrix.mvpntestapp.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.citrix.mvpn.api.MicroVPNSDK;
import com.citrix.mvpntestapp.R;
import com.citrix.sdk.appcore.api.MamSdk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlConnectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_urlconnection);

        new UrlConnectionAsyncTask(this).execute(getIntent().getStringExtra(SelectStartTunnelActivity.URL_KEY));
    }

    private void displayResponse(String reponseHtml) {
        TextView textView = findViewById(R.id.urlConnTextViewId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(reponseHtml, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(reponseHtml));
        }
    }

    private static class UrlConnectionAsyncTask extends AsyncTask<String, Void, String> {
        private static final String LOG_TAG = "MVPN-UrlConnAsyncTask";

        private WeakReference<UrlConnectionActivity> reference;

        private UrlConnectionAsyncTask(UrlConnectionActivity activity) {
            reference = new WeakReference<>(activity);
        }

        private UrlConnectionActivity getActivity() {
            if (reference != null) {
                return reference.get();
            }

            return null;
        }

        @Override
        protected String doInBackground(String... strUrl) {
            StringBuilder response = new StringBuilder();
            Activity activity = getActivity();

            try {
                URL url = new URL(strUrl[0]);
                HttpURLConnection conn;

                if (MicroVPNSDK.isNetworkTunnelRunning(activity)) {
                    conn = (HttpURLConnection) MicroVPNSDK.createURLConnection(activity, url);
                } else {
                    conn = (HttpURLConnection) url.openConnection();
                }

                int responseCode = conn.getResponseCode();
                InputStream inputStream;

                if (200 <= responseCode && responseCode <= 299) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String currentLine;

                while ((currentLine = in.readLine()) != null) {
                    response.append(currentLine);
                }

                in.close();
            } catch (Exception e) {
                MamSdk.getLogger().error(LOG_TAG, e.getMessage());
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String responseHtml) {
            UrlConnectionActivity activity = getActivity();

            if (activity != null && !activity.isFinishing()) {
                activity.displayResponse(responseHtml);
            }
        }
    }
}
