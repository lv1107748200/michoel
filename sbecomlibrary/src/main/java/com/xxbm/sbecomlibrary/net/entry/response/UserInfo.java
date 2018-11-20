package com.xxbm.sbecomlibrary.net.entry.response;


/*
 * lv   2018/9/27 登陆注册 返回数据
 */
public class UserInfo {

    private String act;
    private String sessionkey;
    private String username;
    private String logo;
    private String babylogo;
    private String nickname;
    private String bindwx;
    private String sex;
    private String babynickname;
    private String babysex;
    private String babybirthday;

    private String yzm;//图片验证码
    private String sid;//验证码编号
    private String aid;//专辑 id
    private String p;//分页页码
    private String bid;//播单编号
    private String title;//播单名称
    private String mids;//播单故事 id, 多个依照逗号分割

    private String access_token;
    private String openid;

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBindwx() {
        return bindwx;
    }

    public void setBindwx(String bindwx) {
        this.bindwx = bindwx;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public String getBabylogo() {
        return babylogo;
    }

    public void setBabylogo(String babylogo) {
        this.babylogo = babylogo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBabynickname() {
        return babynickname;
    }

    public void setBabynickname(String babynickname) {
        this.babynickname = babynickname;
    }

    public String getBabysex() {
        return babysex;
    }

    public void setBabysex(String babysex) {
        this.babysex = babysex;
    }

    public String getBabybirthday() {
        return babybirthday;
    }

    public void setBabybirthday(String babybirthday) {
        this.babybirthday = babybirthday;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMids() {
        return mids;
    }

    public void setMids(String mids) {
        this.mids = mids;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
