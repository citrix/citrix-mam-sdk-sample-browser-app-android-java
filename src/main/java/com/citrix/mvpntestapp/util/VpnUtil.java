package com.citrix.mvpntestapp.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Messenger;

import com.citrix.mvpn.api.MicroVPNSDK;
import com.citrix.mvpntestapp.intune.IntuneAuthInfo;
import com.citrix.sdk.appcore.api.MamSdk;

import java.util.List;
import java.util.Map;

public class VpnUtil {
    private static final String TAG = "Mvpn-VpnUtil";

    public static boolean startTunnel(Activity activity, Handler handler, boolean isIntuneSelected) {
        MamSdk.getLogger().info(TAG, "Starting Micro VPN Tunnel....");

        try {
            if (isIntuneSelected) {
                List<Map<String, String>> appConfig = IntuneAuthInfo.getInstance().getAppConfigFullData();
                String accessToken = IntuneAuthInfo.getInstance().getAccessToken();
                MicroVPNSDK.startTunnel(activity, new Messenger(handler), appConfig, accessToken);
            } else {
                MicroVPNSDK.startTunnel(activity, new Messenger(handler));
            }

            return true;
        } catch (Exception e) {
            MamSdk.getLogger().error(TAG, "Failed to start tunnel: " + e.getMessage());
        }

        return false;
    }
}
