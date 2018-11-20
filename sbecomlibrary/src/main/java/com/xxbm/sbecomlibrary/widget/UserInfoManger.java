package com.xxbm.sbecomlibrary.widget;


import com.xxbm.sbecomlibrary.com.UserInforConfig;
import com.xxbm.sbecomlibrary.db.UseData;
import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.xxbm.sbecomlibrary.utils.SPUtils;

/**
 * Created by 吕 on 2018/2/6.
 */

public class UserInfoManger {

    volatile private static UserInfoManger instance = null;

    private String userName;
    private String logo;
    private String NickName;
    private String bindWx;//用户是否绑定微信  1是 0否
    private String sessionkey;// 用户登录成功凭证

    private String firstaudio;  //第一次播放
    private String firstLoad;  //第一次播放
    private String musicId;  //类型的

    private boolean isFirst;


    public static UserInfoManger getInstance(){
        if(instance == null){
            synchronized (UserInfoManger.class) {
                if(instance == null){
                    instance = new UserInfoManger();
                }
            }
        }

        return instance;
    }

    public String getSessionkey(){
        if(null == sessionkey){
            sessionkey = UseData.getUseData().getSessionkey();
        }
        return sessionkey;
    }

    public String getUserName() {
        if(null == userName){
            userName = UseData.getUseData().getUserName();
        }
        return userName;
    }

    public String getNickName() {
        if(null == NickName){
            NickName = SPUtils.getString(UserInforConfig.USERNICKNAME,"").toString();
        }
        return NickName;
    }

    public String getBindWx() {
        if(null == bindWx){
            bindWx = UseData.getUseData().getBindWx();
        }
        return bindWx;
    }

    public String getLogo() {
        if(null == logo){
            logo = SPUtils.getString(UserInforConfig.USERLOGO,"").toString();
        }
        return logo;
    }

    public boolean isFirst(){

        if(CheckUtil.isEmpty(firstLoad)){
            firstLoad = SPUtils.getString(UserInforConfig.USERFIRSTLOAD,"").toString();
        }


        if(CheckUtil.isEmpty(firstLoad)){
            isFirst = true;
        }else {
            isFirst = false;
        }

        return isFirst;

    }

    public String getFirstaudio() {//第一次播放地址
        if(null == firstaudio){
            firstaudio = SPUtils.getString(UserInforConfig.USERFIRSTAUDIO,"").toString();
        }
        return firstaudio;
    }

    public String getMusicId() {
        if(true){
            musicId = SPUtils.getString(UserInforConfig.USERMUSICID,"").toString();
        }
        return musicId;
    }

    public void clear(){
        NickName = null;
        sessionkey = null;
        userName = null;
        bindWx = null;
        firstaudio = null;
        musicId = null;
        logo = null;
    }
    public void clearLogin(){
        UseData.setLogin("","","0");
    }

}
