package fm.qian.michael.net.entry.request;


/*
 * lv   2018/9/27  注册 请求数据
 */
public class Reg {
    private String act;//wxreg(微信注册) smsreg(手机号注册)
    private String access_token;
    private String openid;
    private String username;//手机号
    private String vcode;//短信验证码
    private String yzm;//图片验证码
    private String sid;//验证码编号


    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
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
}
