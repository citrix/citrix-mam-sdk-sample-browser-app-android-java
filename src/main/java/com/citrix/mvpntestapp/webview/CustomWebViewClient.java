package com.citrix.mvpntestapp.webview;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.citrix.sdk.appcore.api.MamSdk;

public class CustomWebViewClient extends WebViewClient {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        MamSdk.getLogger().debug5("CustomWebViewClient", "WebViewClient onPageStarted:" + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        MamSdk.getLogger().debug5("CustomWebViewClient", "WebViewClient onPageFinished:" + url);
    }
}
