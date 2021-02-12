package com.citrix.mvpntestapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.citrix.mvpn.api.MicroVPNSDK;
import com.citrix.mvpn.api.MvpnDefaultHandler;
import com.citrix.mvpntestapp.R;
import com.citrix.mvpntestapp.util.TunnelHandler;
import com.citrix.mvpntestapp.util.UrlUtil;
import com.citrix.mvpntestapp.util.VpnUtil;

public class SelectStartTunnelActivity extends AppCompatActivity implements TunnelHandler.Callback {
    private static String TAG = "MVPN-SelectStartTunnelActivity";

    public static final String INTUNE_COMPANY_PORTAL_SELECTED_KEY = "INTUNE_COMPANY_PORTAL_SELECTED";

    public static final String URL_KEY = "URL";

    private EditText uriText;

    private View progressBar;

    private boolean isIntuneSelected;

    private MvpnDefaultHandler mvpnHandler;

    private static final int DEFAULT_RETRY_COUNT = 3;

    private static int retryCount = DEFAULT_RETRY_COUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isIntuneSelected = getIntent().getBooleanExtra(INTUNE_COMPANY_PORTAL_SELECTED_KEY, false);

        progressBar = findViewById(R.id.progressBar);
        uriText = findViewById(R.id.inputURL);
        uriText.setText(UrlUtil.getSavedUrl(this));
        uriText.setSelection(uriText.getText().length());
    }

    public void onStartTunnelClicked(View view) {
        if (mvpnHandler == null) {
            mvpnHandler = new TunnelHandler(this);
        }

        if (VpnUtil.startTunnel(this, mvpnHandler, isIntuneSelected)) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, R.string.start_tunnel_failed_message, Toast.LENGTH_LONG).show();
        }
    }

    public void onSelectConnectionClicked(View view) {
        Intent intent = new Intent(this, SelectConnectionActivity.class);
        intent.putExtra(SelectStartTunnelActivity.INTUNE_COMPANY_PORTAL_SELECTED_KEY, isIntuneSelected);
        startActivity(intent);
    }

    public void onStopTunnelClicked(View view) {
        MicroVPNSDK.stopTunnel(this);
        int messageId = !MicroVPNSDK.isNetworkTunnelRunning(this) ? R.string.stop_tunnel_success_message : R.string.stop_tunnel_failed_message;
        Toast.makeText(this, messageId, Toast.LENGTH_LONG).show();
    }

    public void onStartTunnelAndSendNetworkRequest(View view) {
        Intent intent = new Intent(this, StartTunnelAndSendNetworkRequestActivity.class);

        if (uriText.getText() != null && !TextUtils.isEmpty(uriText.getText().toString())) {
            String url = uriText.getText().toString();
            intent.putExtra(URL_KEY, url);
            UrlUtil.saveUrl(this, url);
        } else {
            intent.putExtra(URL_KEY, getString(R.string.test_uri));
        }

        intent.putExtra(SelectStartTunnelActivity.INTUNE_COMPANY_PORTAL_SELECTED_KEY, isIntuneSelected);
        startActivity(intent);
    }

    @Override
    public void onTunnelStarted() {
        runOnUiThread(() -> {
            retryCount = DEFAULT_RETRY_COUNT;
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.start_tunnel_success_message, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onError(boolean isSessionExpired) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            if (isSessionExpired && retryCount-- > 0) {
                if (VpnUtil.startTunnel(this, mvpnHandler, isIntuneSelected)) {
                    Toast.makeText(this, R.string.session_expired_message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.start_tunnel_failed_message, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.start_tunnel_failed_message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
