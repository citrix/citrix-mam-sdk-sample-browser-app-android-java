package com.citrix.mvpntestapp.activities;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.citrix.mvpn.api.MicroVPNSDK;
import com.citrix.mvpn.api.MvpnDefaultHandler;
import com.citrix.mvpn.exception.MvpnException;
import com.citrix.mvpn.exception.NetworkTunnelNotStartedException;
import com.citrix.mvpntestapp.R;
import com.citrix.mvpntestapp.util.TunnelHandler;
import com.citrix.mvpntestapp.util.VpnUtil;
import com.citrix.mvpntestapp.webview.CustomWebViewClient;
import com.citrix.sdk.appcore.api.MamSdk;

public class StartTunnelAndSendNetworkRequestActivity extends AppCompatActivity implements TunnelHandler.Callback {
    private static final String TAG = "MVPN-StartTunnelReq";

    private View progressBar;

    private MvpnDefaultHandler mvpnHandler;

    private boolean isIntuneSelected;

    private WebView webView;

    private WebViewClient webViewClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_webview);

        webView = findViewById(R.id.webview);
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setSaveFormData(false);
        webViewClient = new CustomWebViewClient();
        webView.setWebViewClient(webViewClient);

        isIntuneSelected = getIntent().getBooleanExtra(SelectStartTunnelActivity.INTUNE_COMPANY_PORTAL_SELECTED_KEY, false);

        progressBar = findViewById(R.id.progressBar);

        if (MicroVPNSDK.isNetworkTunnelRunning(this)) {
            loadUrl();
        } else {
            progressBar = findViewById(R.id.progressBar);

            if (mvpnHandler == null) {
                mvpnHandler = new TunnelHandler(this);
            }

            if (VpnUtil.startTunnel(this, mvpnHandler, isIntuneSelected)) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, R.string.start_tunnel_failed_message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onTunnelStarted() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            loadUrl();
        });
    }

    @Override
    public void onError(boolean isSessionExpired) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            if (isSessionExpired) {
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

    private void loadUrl() {
        try {
            webView = MicroVPNSDK.enableWebViewObjectForNetworkTunnel(this, webView, webViewClient);
            webView.loadUrl(getIntent().getStringExtra(SelectStartTunnelActivity.URL_KEY));
        } catch (NetworkTunnelNotStartedException nte) {
            MamSdk.getLogger().error(TAG, "Network Tunnel is not running:" + nte.getMessage());
            if (!VpnUtil.startTunnel(this, mvpnHandler, isIntuneSelected)) {
                Toast.makeText(this, R.string.start_tunnel_failed_message, Toast.LENGTH_LONG).show();
            }
        } catch (MvpnException e) {
            MamSdk.getLogger().error(TAG, e.getMessage());
        }
    }
}
