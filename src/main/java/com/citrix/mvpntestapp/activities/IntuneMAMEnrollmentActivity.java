package com.citrix.mvpntestapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.citrix.mvpntestapp.R;
import com.citrix.mvpntestapp.intune.IntuneMAMEnroller;
import com.microsoft.intune.mam.client.app.MAMActivity;

import static android.widget.Toast.LENGTH_LONG;

public class IntuneMAMEnrollmentActivity extends MAMActivity {
    private ProgressDialog progress;

    private IntuneMAMEnroller intuneMAMEnroller = new IntuneMAMEnroller();

    private IntuneMAMEnroller.EnrollmentCallback enrollmentCallback = new IntuneMAMEnroller.EnrollmentCallback() {
        @Override
        public void onMAMEnrollmentStart() {
        }

        @Override
        public void onMAMEnrollmentSuccess() {
            runOnUiThread(() -> {
                dismissProgressDialog();
                Toast.makeText(IntuneMAMEnrollmentActivity.this, R.string.intune_enrollment_success_message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(IntuneMAMEnrollmentActivity.this, SelectStartTunnelActivity.class);
                intent.putExtra(SelectStartTunnelActivity.INTUNE_COMPANY_PORTAL_SELECTED_KEY, true);
                startActivity(intent);
                finish();
            });
        }

        @Override
        public void onMAMEnrollmentError(String message) {
            runOnUiThread(() -> {
                dismissProgressDialog();
                Toast.makeText(IntuneMAMEnrollmentActivity.this, message, LENGTH_LONG).show();
                finish();
            });
        }
    };

    @Override
    public void onMAMCreate(Bundle savedInstanceState) {
        super.onMAMCreate(savedInstanceState);
        setContentView(R.layout.activity_intune_mam_enrollment);

        startProgressDialog(getString(R.string.mam_enrollment_progress_message));

        if (!intuneMAMEnroller.isMAMEnrolled()) {
            intuneMAMEnroller.enroll(this, enrollmentCallback);
        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    intuneMAMEnroller.refreshAuthInfo(IntuneMAMEnrollmentActivity.this);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    enrollmentCallback.onMAMEnrollmentSuccess();
                }
            }.execute();
        }
    }

    @Override
    public void onMAMActivityResult(int requestCode, int resultCode, Intent data) {
        super.onMAMActivityResult(requestCode, resultCode, data);
        intuneMAMEnroller.onActivityResult(requestCode, resultCode, data);
    }

    private void startProgressDialog(String message) {
        progress = new ProgressDialog(this);
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    private void dismissProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }
}
