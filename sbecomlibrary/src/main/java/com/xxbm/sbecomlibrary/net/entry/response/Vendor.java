package com.xxbm.sbecomlibrary.net.entry.response;


/*
 * lv   2018/11/15
 */
public class Vendor {
    private String KeyParam;
    private String VendorAccessId;
    private String VendorAccessSecret;
    private String VendorKey;

    public String getKeyParam() {
        return KeyParam;
    }

    public void setKeyParam(String keyParam) {
        KeyParam = keyParam;
    }

    public String getVendorAccessId() {
        return VendorAccessId;
    }

    public void setVendorAccessId(String vendorAccessId) {
        VendorAccessId = vendorAccessId;
    }

    public String getVendorAccessSecret() {
        return VendorAccessSecret;
    }

    public void setVendorAccessSecret(String vendorAccessSecret) {
        VendorAccessSecret = vendorAccessSecret;
    }

    public String getVendorKey() {
        return VendorKey;
    }

    public void setVendorKey(String vendorKey) {
        VendorKey = vendorKey;
    }
}
