package com.citrix.mvpntestapp.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.citrix.mvpn.api.MicroVPNSDK;
import com.citrix.mvpntestapp.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.citrix.mvpntestapp.activities.MainActivity.URL_KEY;

public class UrlConnectionActivity extends AppCompatActivity {
    private static final String TAG = "UrlConnectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_urlconnection);

        new UrlConnectionAsyncTask(this).execute(getIntent().getStringExtra(URL_KEY));
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
                Log.e(TAG, e.getMessage(), e);
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
