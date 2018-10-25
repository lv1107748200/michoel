package fm.qian.michael.ui.activity;


import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hr.bclibrary.utils.CheckUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.db.UseData;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.request.Reg;
import fm.qian.michael.net.entry.response.UserInfo;
import fm.qian.michael.net.entry.response.WXAccessData;
import fm.qian.michael.net.entry.response.YZMOrSID;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NToast;
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
public class LoginActivity extends BaseIntensifyActivity {

    public static final String LOGIN = "LOGIN";

//    public static final int ONE = 1; //绑定手机
//    public static final int TWO = 2;//手机号登陆
//    public static final int THREE = 3;//手机号注册

    private String type;
    private String act;

    private Disposable mdDisposable;//倒计时

    private YZMOrSID yzmOrSID;
    private WXAccessData wxAccessData;//微信绑定是

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


    @OnClick({R.id.base_left_layout, R.id.base_right_layout,
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

                    if(THREE.equals(type)){
                        login();
                    }else {
                        user_reg();
                    }

                }

              //login();
                break;
            case R.id.send_verification_code://获取验证码
                user_sms();
                break;
            case R.id.verification_code_img://图片验证
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
                    getyzm();//手机号注册时

                    break;
                case THREE:
                    setTitleTv(getString(R.string.手机号登录));
                    tv_message.setText(getString(R.string.手机号登录));
                   // act = "smslogin";
                    getyzm();//手机号登陆

                    break;
                    default:
                        setTitleTv("登陆");
                        break;
            }
        }

    }

    //获取短信验证码时  首先要获取 sid
    private void getyzm(){
        verification_code_img.setEnabled(false);

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
        if(CheckUtil.isEmpty(code)){
            NToast.shortToastBaseApp("短信验证码错误");
            et_verification_code.setShakeAnimation();
            return;
        }
        if(null == yzmOrSID){
            NToast.shortToastBaseApp("重新获取图形验证码");
            return;
        }

        reg.setAct(act);
        reg.setUsername(phone);
        reg.setVcode(code);

        baseService.user_reg(reg, new HttpCallback<UserInfo, BaseDataResponse<UserInfo>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccessAll(BaseDataResponse<UserInfo> k) {
                UserInfo userInfo = k.getData();

                String msg = k.getMsg();
                NToast.shortToastBaseApp("登录成功");

           //     SPUtils.putString(USERNAME,userInfo.getUsername(),true);
         //       SPUtils.putString(USERSESSIONKEY,userInfo.getSessionkey(),true);
//                SPUtils.putString(USERLOGO,userInfo.getLogo(),true);
//                SPUtils.putString(USERNICKNAME,userInfo.getNickname(),true);
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


        String phone = de_login_phone.getText().toString();
        String yzm = de_login_image.getText().toString();
        String code = et_verification_code.getText().toString();

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
        if(CheckUtil.isEmpty(code)){
            NToast.shortToastBaseApp("短信验证码错误");
            et_verification_code.setShakeAnimation();
            return;
        }
        if(null == yzmOrSID){
            NToast.shortToastBaseApp("重新获取图形验证码");
            return;
        }
        Reg reg = new Reg();
        reg.setAct(act);
        reg.setUsername(phone);
        reg.setVcode(code);

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

                 //   SPUtils.putString(USERNAME,userInfo.getUsername(),true);
                  //  SPUtils.putString(USERSESSIONKEY,userInfo.getSessionkey(),true);
//                SPUtils.putString(USERLOGO,userInfo.getLogo(),true);
//                SPUtils.putString(USERNICKNAME,userInfo.getNickname(),true);
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
}
