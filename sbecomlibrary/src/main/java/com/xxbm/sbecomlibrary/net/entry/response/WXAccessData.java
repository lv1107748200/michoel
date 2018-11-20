package com.xxbm.sbecomlibrary.net.entry.response;


import android.os.Parcel;
import android.os.Parcelable;

/*
 * lv   2018/9/27
 */
public class WXAccessData implements Parcelable {


    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;

    private String nickname;
    private String headimgurl;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.access_token);
        dest.writeString(this.expires_in);
        dest.writeString(this.refresh_token);
        dest.writeString(this.openid);
        dest.writeString(this.scope);
        dest.writeString(this.unionid);
        dest.writeString(this.nickname);
        dest.writeString(this.headimgurl);
    }

    public WXAccessData() {
    }

    protected WXAccessData(Parcel in) {
        this.access_token = in.readString();
        this.expires_in = in.readString();
        this.refresh_token = in.readString();
        this.openid = in.readString();
        this.scope = in.readString();
        this.unionid = in.readString();
        this.nickname = in.readString();
        this.headimgurl = in.readString();
    }

    public static final Parcelable.Creator<WXAccessData> CREATOR = new Parcelable.Creator<WXAccessData>() {
        @Override
        public WXAccessData createFromParcel(Parcel source) {
            return new WXAccessData(source);
        }

        @Override
        public WXAccessData[] newArray(int size) {
            return new WXAccessData[size];
        }
    };
}
