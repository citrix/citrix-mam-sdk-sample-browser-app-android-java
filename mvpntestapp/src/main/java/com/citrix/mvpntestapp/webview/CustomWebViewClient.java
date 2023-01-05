/*
 * Copyright (c) 2023. Cloud Software Group, Inc. All Rights Reserved.
 */

package com.citrix.mvpntestapp.webview;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebViewClient extends WebViewClient {
    private static final String TAG = "CustomWebViewClient";

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d(TAG, "WebViewClient onPageStarted:" + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG,"WebViewClient onPageFinished:" + url);
    }
}
