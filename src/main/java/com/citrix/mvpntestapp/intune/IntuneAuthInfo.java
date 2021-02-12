package com.citrix.mvpntestapp.intune;

import java.util.List;
import java.util.Map;

public class IntuneAuthInfo {
    private static IntuneAuthInfo instance = new IntuneAuthInfo();

    private IntuneAuthInfo() {

    }

    public static IntuneAuthInfo getInstance() {
        return instance;
    }

    private String accessToken;

    private List<Map<String, String>> appConfigFullData;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAppConfigFullData(List<Map<String, String>> appConfigFullData) {
        this.appConfigFullData = appConfigFullData;
    }

    public List<Map<String, String>> getAppConfigFullData() {
        return this.appConfigFullData;
    }
}
