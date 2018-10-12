package fm.qian.michael.wxapi;


import com.tencent.mm.opensdk.openapi.IWXAPI;

import fm.qian.michael.common.GlobalVariable;

/*
 * lv   2018/9/27
 */
public class Constants {

           public static final String APP_ID = "wx49b9df93a0b13e78"; //替换为申请到的app id  
           public static final String SECRET = "61123b80cc4875aa2abfae6746c2ae07"; //secret 

           public static IWXAPI wx_api;//全局的微信api对象  
           public static String  type = GlobalVariable.ZERO;//0 登录 1授权 2分享

}
