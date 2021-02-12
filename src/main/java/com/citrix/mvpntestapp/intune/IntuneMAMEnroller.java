package com.citrix.mvpntestapp.intune;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.citrix.mvpn.api.MvpnIntuneConstants;
import com.citrix.sdk.appcore.api.MamSdk;
import com.citrix.sdk.logging.api.LoggingAPI;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.UserInfo;
import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.client.app.startup.ADALConnectionDetails;
import com.microsoft.intune.mam.client.identity.MAMPolicyManager;
import com.microsoft.intune.mam.client.notification.MAMNotificationReceiver;
import com.microsoft.intune.mam.client.notification.MAMNotificationReceiverRegistry;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;
import com.microsoft.intune.mam.policy.MAMServiceAuthenticationCallback;
import com.microsoft.intune.mam.policy.MAMUserInfo;
import com.microsoft.intune.mam.policy.appconfig.MAMAppConfig;
import com.microsoft.intune.mam.policy.appconfig.MAMAppConfigManager;
import com.microsoft.intune.mam.policy.notification.MAMEnrollmentNotification;
import com.microsoft.intune.mam.policy.notification.MAMNotificationType;

import java.util.List;
import java.util.Map;

import static com.microsoft.intune.mam.policy.MAMEnrollmentManager.Result.ENROLLMENT_SUCCEEDED;

public class IntuneMAMEnroller {
    private static final String TAG = "IntuneMAMEnroller";

    private AuthenticationContext authenticationContext;

    private ADALConnectionDetails adalConnectionDetails;

    private MAMEnrollmentManager mamEnrollmentManager = MAMComponents.get(MAMEnrollmentManager.class);

    private MAMUserInfo mamUserInfo = MAMComponents.get(MAMUserInfo.class);

    private EnrollmentCallback enrollmentCallback;

    private static LoggingAPI LOGGER = MamSdk.getLogger();

    public interface EnrollmentCallback {
        void onMAMEnrollmentStart();

        void onMAMEnrollmentSuccess();

        void onMAMEnrollmentError(String message);
    }

    private MAMServiceAuthenticationCallback mamServiceAuthenticationCallback = new MAMServiceAuthenticationCallback() {
        public String acquireToken(String upn, String aadId, String resourceId) {
            try {
                return authenticationContext.acquireTokenSilentSync(resourceId, getClientID(), aadId).getAccessToken();
            } catch (Exception e) {
                LOGGER.info(TAG, e.getMessage());
            }

            return null;
        }
    };

    private String getClientID() {
        return adalConnectionDetails.getClientId();
    }

    private MAMNotificationReceiver mamNotificationReceiver = mamNotification -> {
        MAMEnrollmentNotification enrollmentNotification = (MAMEnrollmentNotification) mamNotification;
        invokeEnrollmentCallback(enrollmentNotification.getEnrollmentResult());
        return true;
    };

    private void invokeEnrollmentCallback(MAMEnrollmentManager.Result result) {
        LOGGER.info(TAG, "MAM enrollment result=" + result);

        if (result == ENROLLMENT_SUCCEEDED) {
            enrollmentCallback.onMAMEnrollmentSuccess();
            IntuneAuthInfo.getInstance().setAppConfigFullData(getAppConfigFullData());
        } else if (result != MAMEnrollmentManager.Result.PENDING) {
            enrollmentCallback.onMAMEnrollmentError("Intune MAM enrollment failed: " + result);
        }
    }

    private List<Map<String, String>> getAppConfigFullData() {
        String primaryUser = mamUserInfo.getPrimaryUser();

        if (TextUtils.isEmpty(primaryUser)) {
            return null;
        }

        MAMAppConfigManager configManager = MAMComponents.get(MAMAppConfigManager.class);
        MAMAppConfig appConfig = configManager.getAppConfig(primaryUser);

        return appConfig.getFullData();
    }

    public void enroll(Activity activity, EnrollmentCallback callback) {
        this.enrollmentCallback = callback;

        if (isIntuneUserEnrolled() && !isAppIntuneRegistered()) {
            this.enrollmentCallback.onMAMEnrollmentError("This app is not MAM enrolled!");
        } else {
            enroll(activity);
        }
    }

    private void enroll(Activity activity) {
        this.authenticationContext = new AuthenticationContext(activity, ADALConnectionDetails.DEFAULT_AUTHORITY, true);
        this.adalConnectionDetails = ADALConnectionDetails.getAppManifestConnectionDetails(activity.getPackageName(), activity.getPackageManager());
        this.mamEnrollmentManager.registerAuthenticationCallback(mamServiceAuthenticationCallback);
        acquireEnrollmentToken(activity);
    }

    private void acquireEnrollmentToken(Activity activity) {
        authenticationContext.acquireToken(activity,
                ADALConnectionDetails.RESOURCE_ID, adalConnectionDetails.getClientId(),
                adalConnectionDetails.getNonBrokerRedirectUri(), getPrimaryUser(),
                new AuthenticationCallback<AuthenticationResult>() {
                    @Override
                    public void onSuccess(AuthenticationResult result) {
                        LOGGER.info(TAG, "Adal logon for enrollment token is successful.");
                        acquireVpnToken(activity);
                    }

                    @Override
                    public void onError(Exception exc) {
                        LOGGER.error(TAG, "Adal logon for enrollment token failed.", exc);
                        enrollmentCallback.onMAMEnrollmentError(exc.getMessage());
                    }
                }
        );
    }

    private void acquireVpnToken(Activity activity) {
        authenticationContext.acquireToken(activity,
                MvpnIntuneConstants.NSG_MULTI_TENANT_AAD_RESOURCE, MvpnIntuneConstants.NSG_MULTI_TENANT_AAD_CLIENT_ID,
                adalConnectionDetails.getNonBrokerRedirectUri(), getPrimaryUser(),
                new AuthenticationCallback<AuthenticationResult>() {
                    @Override
                    public void onSuccess(AuthenticationResult result) {
                        LOGGER.info(TAG, "Adal logon for vpn authentication token is successful.");
                        IntuneAuthInfo.getInstance().setAccessToken(result.getAccessToken());
                        enrollmentCallback.onMAMEnrollmentStart();
                        invokeEnrollmentCallback(enrollApplication(result));
                    }

                    @Override
                    public void onError(Exception exc) {
                        LOGGER.error(TAG, "Adal logon for vpn authentication token failed.", exc);
                        enrollmentCallback.onMAMEnrollmentError(exc.getMessage());
                    }
                }
        );
    }

    private String getPrimaryUser() {
        String primaryUser = mamUserInfo.getPrimaryUser();
        return primaryUser == null ? "" : primaryUser;
    }

    private MAMEnrollmentManager.Result enrollApplication(AuthenticationResult authenticationResult) {
        UserInfo userInfo = authenticationResult.getUserInfo();
        registerMAMEnrollmentListener();
        mamEnrollmentManager.registerAccountForMAM(userInfo.getDisplayableId(), userInfo.getUserId(), authenticationResult.getTenantId());
        return mamEnrollmentManager.getRegisteredAccountStatus(userInfo.getDisplayableId());
    }

    private void registerMAMEnrollmentListener() {
        MAMNotificationReceiverRegistry registry = MAMComponents.get(MAMNotificationReceiverRegistry.class);
        registry.registerReceiver(mamNotificationReceiver, MAMNotificationType.MAM_ENROLLMENT_RESULT);
    }

    private boolean isIntuneUserEnrolled() {
        return !TextUtils.isEmpty(mamUserInfo.getPrimaryUser());
    }

    private boolean isAppIntuneRegistered() {
        String primaryUser = mamUserInfo.getPrimaryUser();

        if (TextUtils.isEmpty(primaryUser)) {
            return false;
        }

        return MAMPolicyManager.getIsIdentityManaged(primaryUser);
    }

    public boolean isMAMEnrolled() {
        if (isAppIntuneRegistered()) {
            return mamEnrollmentManager.getRegisteredAccountStatus(mamUserInfo.getPrimaryUser()) == ENROLLMENT_SUCCEEDED;
        }

        return false;
    }

    public void refreshAuthInfo(Context context) {
        this.authenticationContext = new AuthenticationContext(context, ADALConnectionDetails.DEFAULT_AUTHORITY, true);

        try {
            String accessToken = authenticationContext.acquireTokenSilentSync(MvpnIntuneConstants.NSG_MULTI_TENANT_AAD_RESOURCE,
                    MvpnIntuneConstants.NSG_MULTI_TENANT_AAD_CLIENT_ID, getPrimaryUser()).getAccessToken();
            IntuneAuthInfo.getInstance().setAccessToken(accessToken);
        } catch (Exception e) {
            LOGGER.info(TAG, "" + e.getMessage());
        }

        IntuneAuthInfo.getInstance().setAppConfigFullData(getAppConfigFullData());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (authenticationContext != null) {
            authenticationContext.onActivityResult(requestCode, resultCode, data);
        }
    }
}
