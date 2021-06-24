package com.citrix.mvpntestapp.util;

import android.os.Message;

//import com.citrix.mvpn.api.MvpnDefaultHandler;

/*
public class TunnelHandler extends MvpnDefaultHandler {
    private Callback callback;

    public interface Callback {
        void onTunnelStarted();

        void onError(boolean isSessionExpired);
    }

    public TunnelHandler(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (callback != null) {
            if (isNetworkTunnelRunning()) {
                callback.onTunnelStarted();
            } else {
                callback.onError(isSessionExpired());
            }
        }
    }
}
*/