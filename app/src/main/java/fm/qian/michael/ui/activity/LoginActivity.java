package fm.qian.michael.ui.activity;


import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alicom.phonenumberauthsdk.gatewayauth.AlicomAuthHelper;
import com.alicom.phonenumberauthsdk.gatewayauth.TokenResultListener;
import com.alicom.phonenumberauthsdk.gatewayauth.model.InitResult;
import com.alicom.phonenumberauthsdk.gatewayauth.model.VendorConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.db.UseData;
import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.entry.request.Reg;
import com.xxbm.sbecomlibrary.net.entry.response.ALiYan;
import com.xxbm.sbecomlibrary.net.entry.response.AccessCodeMsg;
import com.xxbm.sbecomlibrary.net.entry.response.UserInfo;
import com.xxbm.sbecomlibrary.net.entry.response.Vendor;
import com.xxbm.sbecomlibrary.net.entry.response.WXAccessData;
import com.xxbm.sbecomlibrary.net.entry.response.YZMOrSID;
import com.xxbm.sbecomlibrary.net.http.HttpCallback;
import com.xxbm.sbecomlibrary.net.http.HttpException;
import com.xxbm.sbecomlibrary.net.http.HttpUtils;
import com.xxbm.musiclibrary.MusicPlayerManger;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.utils.SPUtils;
import fm.qian.michael.utils.SpanUtils;
import fm.qian.michael.widget.RoundedImage.RoundedImageView;
import fm.qian.michael.widget.custom.ClearWriteEditText;
import fm.qian.michael.widget.single.UserInfoManger;
import fm.qian.michael.wxapi.Constants;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

import static fm.qian.michael.common.GlobalVariable.ONE;
import static fm.qian.michael.common.GlobalVariable.THREE;
import static fm.qian.michael.common.GlobalVariable.TWO;
import static fm.qian.michael.common.UserInforConfig.USERBINDWX;
import static fm.qian.michael.common.UserInforConfig.USERLOGO;
import static fm.qian.michael.common.UserInforConfig.USERNAME;
import static fm.qian.michael.common.UserInforConfig.USERNICKNAME;
import static fm.qian.michael.common.UserInforConfig.USERSESSIONKEY;

/*
 * lv   2018/9/26
 */
public class LoginActivity extends BaseIntensifyActivity implements TokenResultListener {

    public static final String LOGIN = "LOGIN";

//    public static final int ONE = 1; //绑定手机
//    public static final int TWO = 2;//手机号登陆
//    public static final int THREE = 3;//手机号注册

    private String type;
    private String act;

    private Disposable mdDisposable;//倒计时

    private YZMOrSID yzmOrSID;
    private WXAccessData wxAccessData;//微信绑定是
    private AlicomAuthHelper authHelper;
    private boolean isALi;
    private String simPhone;
    private String vCode;

    @BindView(R.id.yz_layout)
    LinearLayout yz_layout;//验证区域
    @BindView(R.id.user_layout)
    LinearLayout user_layout;//用户
    @BindView(R.id.weixin_login_layout)
    LinearLayout weixin_login_layout;//微信登陆
    @BindView(R.id.head_portrait)
    RoundedImageView head_portrait;//头像
    @BindView(R.id.nickname)
    TextView  nickname;//名字
    @BindView(R.id.tv_message)
    TextView  tv_message;//功能介绍
    @BindView(R.id.send_verification_code)
    TextView  send_verification_code;//短信验证码

    @BindView(R.id.de_login_phone)
    ClearWriteEditText de_login_phone;//手机号
    @BindView(R.id.de_login_image)
    ClearWriteEditText de_login_image;//图片验证码
    @BindView(R.id.et_verification_code)
    ClearWriteEditText et_verification_code;//短信验证码

    @BindView(R.id.verification_code_img)//图片验证
    ImageView verification_code_img;

    @BindView(R.id.help_tv)
    TextView help_tv;

    @BindView(R.id.de_login_sign)
    Button de_login_sign;

    @OnClick({
            R.id.send_verification_code,R.id.weixin_login_layout
    ,R.id.de_login_sign,R.id.verification_code_img,R.id.help_tv})
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.base_left_layout:
                finish();
                break;
            case R.id.base_right_layout:

                break;
            case R.id.de_login_sign://登录按钮

                if(null != type){

                    if(isALi){
                        user_gateway();
                    }else {
                        if(THREE.equals(type)){
                            login();
                        }else {
                            user_reg();
                        }
                    }

                }

              //login();
                break;
            case R.id.send_verification_code://获取验证码
                user_sms();
                break;
            case R.id.verification_code_img://图片验证
                verification_code_img.setEnabled(false);
                getyzm();
                break;
            case R.id.weixin_login_layout://微信登陆

                CommonUtils.weixinLogin(this,GlobalVariable.ZERO,true);

                break;
            case R.id.help_tv:
                Intent intent = new Intent(this, WebTBSParticularsActivity.class);
                intent.putExtra(WebTBSParticularsActivity.WEBTYPE, GlobalVariable.FOUR);
                startActivity(intent);
                break;
        }
    }
    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }
    @Override
    public void initView() {
        super.initView();

        SpanUtils spanUtils = new SpanUtils();

        help_tv.setText(spanUtils.
                append("我已阅读并同意")
                .append("《钱儿频道软件服务协议》")
                .setForegroundColor(ContextCompat.getColor(this,R.color.color_F86))
                .setUnderline()
                .create());

        Intent intent = getIntent();
        type = intent.getStringExtra(LOGIN);
        if(null != type){
            switch (type){
                case ONE://
                    setTitleTv(getString(R.string.绑定手机));
                    tv_message.setText(getString(R.string.绑定手机));
                    user_layout.setVisibility(View.VISIBLE);
                    weixin_login_layout.setVisibility(View.GONE);

                    wxAccessData = intent.getParcelableExtra("WXAccessData");

                    getyzm();//微信绑定

                    if(null != wxAccessData){
                        nickname.setText(wxAccessData.getNickname());

                        GlideUtil.setGlideImage(this,CommonUtils.setString(wxAccessData.getHeadimgurl(),"\\/","/"),
                                head_portrait,R.drawable.myicon);
                    }

                    break;
                case TWO:
                    setTitleTv(getString(R.string.手机号注册));
                    tv_message.setText(getString(R.string.手机号注册));
                   // act = "reg";
//                    getyzm();//手机号注册时
                    initALiuthHelper();


                    break;
                case THREE:
                    setTitleTv(getString(R.string.手机号登录));
                    tv_message.setText(getString(R.string.手机号登录));
                   // act = "smslogin";
                   // getyzm();//手机号登陆
                    initALiuthHelper();


                    break;
                    default:
                        setTitleTv("登陆");
                        break;
            }
        }

    }

    private void initALiuthHelper(){


         authHelper = AlicomAuthHelper.getInstance(BaseApplation.getBaseApp(), this);

        //设置debug模式，debug模式会输出调试log
        authHelper.setDebugMode(true);
// 初始化接口，进行网络环境判断和SIM号码读取尝试。
        InitResult autInitResult = authHelper.init();

        if (autInitResult != null) {
            NLog.e(NLog.TAG,"---> 版本Version: " + authHelper.getVersion());
            NLog.e(NLog.TAG,"---> App包名: " + CommonUtils.getPackageName(BaseApplation.getBaseApp()));
            NLog.e(NLog.TAG,"---> App签名: " );

            // 网络环境是否支持网关认证
            if (!autInitResult.isCan4GAuth()) {
                //TODO: do something when can't use 4g auth
                setRenZhengHou();
                return;
            }

            isALi = true;
            yz_layout.setVisibility(View.INVISIBLE);
            de_login_sign.setText("下一步");
            //读取SIM手机号
            if (!TextUtils.isEmpty(autInitResult.getSimPhoneNumber()) && autInitResult.getSimPhoneNumber().length() == 11) {
                simPhone = autInitResult.getSimPhoneNumber();
                if(!CheckUtil.isEmpty(simPhone))
                de_login_phone.setText(simPhone);
                //TODO: do something when read sim card Phone number success，such as set Phone number to input edit。
            }
        }
// 业务方调用业务服务端获取vendorConfig 列表
// List<VendorConfigDTOs> list;
//结果在tokenLinsten中返回
    //    authHelper.getAuthToken(list);
    }

    private void setRenZhengHou(){
        dissLoadingDialog();
        de_login_sign.setText(getString(R.string.Login));
        isALi = false;
        if(null != yz_layout){
            yz_layout.setVisibility(View.VISIBLE);
        }
        getyzm();//手机号注册时 网管认证失败
    }

    //获取短信验证码时  首先要获取 sid
    private void getyzm(){

        String sid = null;
        String random = null;

        if(null != yzmOrSID){
            sid = yzmOrSID.getSid();
            random = CommonUtils.getRandom()+"";
        }

        baseService.getyzm(sid, random,new HttpCallback<YZMOrSID, BaseDataResponse<YZMOrSID>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
                verification_code_img.setEnabled(true);
            }

            @Override
            public void onSuccess(YZMOrSID yzmOrSIDd) {
                verification_code_img.setEnabled(true);
               LoginActivity.this. yzmOrSID = yzmOrSIDd;
                GlideUtil.setGlideImageMake(
                        LoginActivity.this,
                        yzmOrSID.getYzmsrc(),
                        verification_code_img);

            }
        },LoginActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }
    //获取短信
    private void  user_sms(){
        Reg reg = new Reg();
        String yzm = de_login_image.getText().toString();
        String phone = de_login_phone.getText().toString();

        String act = null;

        if(null != type){

            if(TWO .equals(type)){
                act = "reg";//注册时获取验证码
            }else  if(THREE.equals(type)){
                act = "login";//登陆时获取验证码
            }else if (ONE.equals(type)){//微信绑定 按注册走
                act = "reg";
            }

        }

        if(CheckUtil.isEmpty(phone)){
            NToast.shortToastBaseApp("电话号码错误");
            de_login_phone.setShakeAnimation();
            return;
        }
        if(CheckUtil.isEmpty(yzm)){
            NToast.shortToastBaseApp("图形验证码错误");
            de_login_image.setShakeAnimation();
            return;
        }
        if(null == yzmOrSID){
            NToast.shortToastBaseApp("重新获取图形验证码");
            return;
        }

        reg.setAct(act);
        reg.setYzm(yzm);
        reg.setUsername(phone);
        reg.setSid(yzmOrSID.getSid());

        send_verification_code.setEnabled(false);

        baseService.user_sms(reg, new HttpCallback<Object, BaseDataResponse>() {
            @Override
            public void onError(HttpException e) {
                send_verification_code.setEnabled(true);
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(Object userInfo) {
                setMdDisposable();
                NToast.shortToastBaseApp("短信发送成功");

            }
        },LoginActivity.this.bindUntilEvent(ActivityEvent.DESTROY));

    }

    //注册  手机号注册  和 微信注册
    private void user_reg(){

        String act = null;

        Reg reg = new Reg();

        if(null != type){

            if(TWO.equals(type)){
                act = "smsreg";//手机号注册
            }else if(ONE.equals(type)){
                act = "wxreg";//微信注册

                if(null != wxAccessData){
                    reg.setOpenid(wxAccessData.getOpenid());
                    reg.setAccess_token(wxAccessData.getAccess_token());
                }
            }

        }

        String phone = de_login_phone.getText().toString();
        String code = et_verification_code.getText().toString();

        if(CheckUtil.isEmpty(phone)){
            NToast.shortToastBaseApp("电话号码错误");
            de_login_phone.setShakeAnimation();
            return;
        }
        if(isALi){
            code = vCode;
            reg.setCodetype("gateway");
        }else {
            if(CheckUtil.isEmpty(code)){
                NToast.shortToastBaseApp("短信验证码错误");
                et_verification_code.setShakeAnimation();
                return;
            }
            if(null == yzmOrSID){
                NToast.shortToastBaseApp("重新获取图形验证码");
                return;
            }
        }


        reg.setAct(act);
        reg.setUsername(phone);
        reg.setVcode(code);


        baseService.user_reg(reg, new HttpCallback<UserInfo, BaseDataResponse<UserInfo>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
                dissLoadingDialog();
            }

            @Override
            public void onSuccessAll(BaseDataResponse<UserInfo> k) {
                dissLoadingDialog();
                UserInfo userInfo = k.getData();

                String msg = k.getMsg();
                NToast.shortToastBaseApp("登录成功");

           //     SPUtils.putString(USERNAME,userInfo.getUsername(),true);
         //       SPUtils.putString(USERSESSIONKEY,userInfo.getSessionkey(),true);
                SPUtils.putString(USERLOGO,userInfo.getLogo(),false);
                SPUtils.putString(USERNICKNAME,userInfo.getNickname(),false);
          //      SPUtils.putString(USERBINDWX,userInfo.getBindwx(),true);

                UseData.setLogin(userInfo.getUsername(),userInfo.getSessionkey(),userInfo.getBindwx());
                UserInfoManger.getInstance().clear();//每次登陆清空数据
                MusicPlayerManger.login(0);

                EventBus.getDefault().post(new Event.LoginEvent(GlobalVariable.TWO));
                finish();

            }
        },LoginActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    private void login(){


        if(THREE.equals(type)){
            act = "smslogin";//登陆时获取验证码
        }

        Reg reg = new Reg();
        String phone = de_login_phone.getText().toString();
        String yzm = de_login_image.getText().toString();
        String code = et_verification_code.getText().toString();

        if(CheckUtil.isEmpty(phone)){
            NToast.shortToastBaseApp("电话号码错误");
            de_login_phone.setShakeAnimation();
            return;
        }
        if(isALi){
            code = vCode;
            reg.setCodetype("gateway");
        }else {
            if(CheckUtil.isEmpty(yzm)){
                NToast.shortToastBaseApp("图形验证码错误");
                de_login_image.setShakeAnimation();
                return;
            }
            if(CheckUtil.isEmpty(code)){
                NToast.shortToastBaseApp("短信验证码错误");
                et_verification_code.setShakeAnimation();
                return;
            }
            if(null == yzmOrSID){
                NToast.shortToastBaseApp("重新获取图形验证码");
                return;
            }
        }


        reg.setAct(act);
        reg.setUsername(phone);
        reg.setVcode(code);


        baseService.login(reg, new HttpCallback<UserInfo, BaseDataResponse<UserInfo>>() {
            @Override
            public void onError(HttpException e) {
                dissLoadingDialog();
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccessAll(BaseDataResponse<UserInfo> k) {
                dissLoadingDialog();
                UserInfo userInfo = k.getData();

                String msg = k.getMsg();

                if("succ_yes".equals(msg)){
                    NToast.shortToastBaseApp("登录成功");

                 //   SPUtils.putString(USERNAME,userInfo.getUsername(),true);
                  //  SPUtils.putString(USERSESSIONKEY,userInfo.getSessionkey(),true);
                SPUtils.putString(USERLOGO,userInfo.getLogo(),false);
                SPUtils.putString(USERNICKNAME,userInfo.getNickname(),false);
                  //  SPUtils.putString(USERBINDWX,userInfo.getBindwx(),true);
                    UseData.setLogin(userInfo.getUsername(),userInfo.getSessionkey(),userInfo.getBindwx());
                    UserInfoManger.getInstance().clear();//每次登陆清空数据
                    MusicPlayerManger.login(0);

                    EventBus.getDefault().post(new Event.LoginEvent(GlobalVariable.TWO));
                    finish();

                }else if("succ_no".equals(msg)){

                    NToast.shortToastBaseApp("用户没有注册");

                }

            }

            @Override
            public void onSuccess(UserInfo userInfo) {


            }
        },LoginActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    //网关认证
    private void user_gateway(){
        String phone = de_login_phone.getText().toString();

        if(CheckUtil.isEmpty(phone)){
            NToast.shortToastBaseApp("电话号码错误");
            de_login_phone.setShakeAnimation();
            return;
        }
        showLoadingDialog("");
        Reg reg = new Reg();
        reg.setAct("init");
        reg.setOs("android");
        reg.setPhone(phone);

        baseService.user_gateway(reg, new HttpCallback<List<Vendor>, BaseDataResponse<List<Vendor>>>() {
            @Override
            public void onError(HttpException e) {
                setRenZhengHou();
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(List<Vendor> aLiYans) {

                if(null != authHelper){
                    List<VendorConfig> vendorConfigs = HttpUtils.jsonToBeanT(HttpUtils.getStringValue(aLiYans)
                            , new TypeReference<List<VendorConfig>>() {});

                    NLog.e(NLog.TAG,"---> json " +vendorConfigs.get(0).toString());

                    if(!CheckUtil.isEmpty(vendorConfigs))
                    authHelper.getAuthToken(vendorConfigs);
                }

            }
        },LoginActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }
    //得到验证码
    private void user_gatewayObject(final String acc){
        String phone = de_login_phone.getText().toString();

        if(CheckUtil.isEmpty(phone)){
            NToast.shortToastBaseApp("电话号码错误");
            de_login_phone.setShakeAnimation();
            return;
        }
        Reg reg = new Reg();

        reg.setAct("verify");
        reg.setOs("android");
        reg.setPhone(phone);
        reg.setAccesscode(acc);

        baseService.user_gatewayObject(reg, new HttpCallback<Reg, BaseDataResponse<Reg>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
                setRenZhengHou();
            }

            @Override
            public void onSuccess(Reg aLiYans) {

                vCode = aLiYans.getVcode();

                if(THREE.equals(type)){
                    login();
                }else {
                    user_reg();
                }

            }
        },LoginActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    private void setMdDisposable(){
        //从0开始发射11个数字为：0-10依次输出，延时0s执行，每1s发射一次。

        if(null != mdDisposable){
            if(!mdDisposable.isDisposed()){
                mdDisposable.dispose();
            }
        }

        mdDisposable = Flowable.intervalRange(0, 60, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        send_verification_code.setText( (60 - aLong) + "s");
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //倒计时完毕置为可点击状态
                        send_verification_code.setEnabled(true);
                        send_verification_code.setText(getString(R.string.获取验证码));
                    }
                })
                .subscribe();

    }


    @Override
    protected void onDestroy() {
        if(null != authHelper){
            authHelper.onDestroy();
            authHelper = null;
        }
        super.onDestroy();

        if(null != mdDisposable){
            mdDisposable.dispose();
            mdDisposable = null;
        }
    }


    private void weixinLogin(){
        Constants.wx_api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        Constants.wx_api.registerApp(Constants.APP_ID);

        if (!Constants.wx_api.isWXAppInstalled()) {
            NToast.shortToastBaseApp("未安装微信");
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            Constants.wx_api.sendReq(req);

            finish();
        }

    }

    // FIXME: 2018/11/13  阿里免输入验证码校验
    @Override
    public void onTokenSuccess(final String s) {
        NLog.e(NLog.TAG,"onTokenSuccess ---> " + s);
        final AccessCodeMsg accessCodeMsg = HttpUtils.jsonToBean(s,AccessCodeMsg.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null != accessCodeMsg){
                    user_gatewayObject(accessCodeMsg.getAccessCode());
                }
            }
        });
    }

    @Override
    public void onTokenFailed(String s) {
        NLog.e(NLog.TAG,"onTokenFailed ---> " + s);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRenZhengHou();
            }
        });

    }
}
