package com.xxbm.sbecomlibrary.net.entry.response;


/*
 * lv   2018/11/16
 */
public class AccessCodeMsg {
    private String accessCode;
    private String deviceType;
    private String osType;
    private String vendor_key;

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getVendor_key() {
        return vendor_key;
    }

    public void setVendor_key(String vendor_key) {
        this.vendor_key = vendor_key;
    }
}
