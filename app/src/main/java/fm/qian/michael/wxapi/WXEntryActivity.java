package fm.qian.michael.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.db.UseData;
import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.entry.request.Reg;
import com.xxbm.sbecomlibrary.net.entry.response.UserInfo;
import com.xxbm.sbecomlibrary.net.entry.response.WXAccessData;
import com.xxbm.sbecomlibrary.net.http.HttpCallback;
import com.xxbm.sbecomlibrary.net.http.HttpException;
import com.xxbm.musiclibrary.MusicPlayerManger;
import fm.qian.michael.ui.activity.LoginActivity;
import fm.qian.michael.utils.NLog;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.utils.SPUtils;
import fm.qian.michael.widget.single.UserInfoManger;

import static fm.qian.michael.common.GlobalVariable.THREE;
import static fm.qian.michael.common.GlobalVariable.TWO;
import static fm.qian.michael.common.UserInforConfig.USERBINDWX;
import static fm.qian.michael.common.UserInforConfig.USERLOGO;
import static fm.qian.michael.common.UserInforConfig.USERNAME;
import static fm.qian.michael.common.UserInforConfig.USERNICKNAME;
import static fm.qian.michael.common.UserInforConfig.USERSESSIONKEY;
import static fm.qian.michael.wxapi.Constants.APP_ID;
import static fm.qian.michael.wxapi.Constants.SECRET;
import static fm.qian.michael.wxapi.Constants.type;

/*
 * lv   2018/9/27
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private String access_token;
    private String openid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constants.wx_api.handleIntent(getIntent(), this);

    }

    @Override
    public int getLayout() {
        return R.layout.activity_weixin;
    }

    @Override
    public void initView() {
        super.initView();
        setTranslucentStatus(true);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

        NLog.e(NLog.TAGOther,"微信登陆 errCode = " + baseResp.errCode );
        //登录回调
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:

                if(GlobalVariable.TWO.equals(Constants.type)){
                    NToast.shortToastBaseApp("分享成功");
                    finish();
                }else {
                    String code = ((SendAuth.Resp) baseResp).code;
                    //获取用户信息
                    NLog.e(NLog.TAGOther,"微信登陆 code = " + code );



                    access_token(code);
                }

                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                finish();
                break;
            default:
                finish();
                break;
        }
    }


    private void access_token(String Code){
        if(CheckUtil.isEmpty(Code))
            return;

        baseService.access_token(APP_ID, SECRET, Code, new HttpCallback<WXAccessData, WXAccessData>() {

            @Override
            public void onError(HttpException e) {

                NToast.shortToastBaseApp(e.getMsg());

            }

            @Override
            public void onSuccess(WXAccessData o) {
               // finish();
               // NToast.shortToastBaseApp(o.getAccess_token());

                access_token = o.getAccess_token();
                openid = o.getOpenid();

               // userinfo();

                if(GlobalVariable.ZERO.equals(Constants.type)){
                    login();
                }else if(GlobalVariable.ONE.equals(Constants.type)){
                    user_bind();
                }

            }
        },WXEntryActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }




    private void login(){
        Reg reg = new Reg();
        reg.setAct("wxlogin");

        reg.setAccess_token(access_token);
        reg.setOpenid(openid);

        baseService.login(reg, new HttpCallback<UserInfo, BaseDataResponse<UserInfo>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccessAll(BaseDataResponse<UserInfo> k) {
                UserInfo userInfo = k.getData();

                String msg = k.getMsg();

                if("succ_yes".equals(msg)){
                    NToast.shortToastBaseApp("登录成功");

                  //  SPUtils.putString(USERNAME,userInfo.getUsername(),true);
                 //   SPUtils.putString(USERSESSIONKEY,userInfo.getSessionkey(),true);
                SPUtils.putString(USERLOGO,userInfo.getLogo(),false);
                SPUtils.putString(USERNICKNAME,userInfo.getNickname(),false);
                 //   SPUtils.putString(USERBINDWX,userInfo.getBindwx(),true);
                    UseData.setLogin(userInfo.getUsername(),userInfo.getSessionkey(),userInfo.getBindwx());
                    UserInfoManger.getInstance().clear();//每次登陆清空数据
                    MusicPlayerManger.login(0);

                    EventBus.getDefault().post(new Event.LoginEvent(GlobalVariable.TWO));
                    finish();
                }else if("succ_no".equals(msg)){
                  //  NToast.shortToastBaseApp("用户没有注册");

                    userinfo();

                }

            }

            @Override
            public void onSuccess(UserInfo userInfo) {


            }
        },WXEntryActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    private void userinfo(){
        baseService.userinfo(access_token, openid, new HttpCallback<WXAccessData, WXAccessData>() {
            @Override
            public void onError(HttpException e) {

            }

            @Override
            public void onSuccess(WXAccessData wxAccessData) {

                wxAccessData.setAccess_token(access_token);
                wxAccessData.setOpenid(openid);

                Intent intent = new Intent(WXEntryActivity.this,LoginActivity.class);
                intent.putExtra(LoginActivity.LOGIN,GlobalVariable.ONE);
                intent.putExtra("WXAccessData",wxAccessData);
                startActivity(intent);

                finish();

            }
        },WXEntryActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    private void user_bind(){

        UserInfo userInfo = new UserInfo();

        userInfo.setOpenid(openid);
        userInfo.setAccess_token(access_token);

        userInfo.setUsername(UserInfoManger.getInstance().getUserName());
        userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());

        baseService.user_bind(userInfo, new HttpCallback<Object, BaseDataResponse<Object>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
                finish();
            }

            @Override
            public void onSuccessAll(BaseDataResponse<Object> k) {
           //     SPUtils.putString(USERBINDWX,"1",true);

                UseData useData = UseData.getUseData();
                useData.setBindWx("1");
                useData.update();

                UserInfoManger.getInstance().clear();//每次登陆清空数据
                EventBus.getDefault().post(new Event.LoginEvent(GlobalVariable.ZERO));

                finish();

            }
        },WXEntryActivity.this.bindUntilEvent(ActivityEvent.DESTROY));


    }

    @Override
    protected void onDestroy() {

        Constants.wx_api.unregisterApp();
        Constants.wx_api = null;
        Constants.type = null;

        super.onDestroy();
    }
}
