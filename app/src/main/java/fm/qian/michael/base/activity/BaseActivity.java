package fm.qian.michael.base.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;



import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.hr.bclibrary.utils.CheckUtil;
import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.net.base.BaseService;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.broadcast.NetworkConnectChangedReceiver;
import fm.qian.michael.widget.single.UserInfoManger;


/**
 * Created by 吕 on 2017/10/26.
 */

public class BaseActivity extends AbstractBaseActivity{
    @Inject
    public BaseService baseService;
    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //setStatusBar();
        super.onCreate(savedInstanceState);
        BaseApplation.getBaseApp().getAppComponent().inject(this);
        setContentView(getLayout());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        unbinder =  ButterKnife.bind(this);
        initTitle();
        initView();
        initData();
    }

    /**
     * @param outState 取消保存状态
     */
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    /**
     * @param savedInstanceState 取消保存状态
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public int  getLayout() {
        return 0;
    }



    @Override
    public void initTitle() {

    }
    @Override
    public void initView() {

    }
    @Override
    public void initData() {

    }

    //设置状态栏状态
    public void setStatusBar(View mStatusBar){
        if(null == mStatusBar)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            mStatusBar.setVisibility(View.VISIBLE);
            mStatusBar.getLayoutParams().height = DisplayUtils.getStatusBarHeight(this);
            mStatusBar.setLayoutParams(mStatusBar.getLayoutParams());
        }else {
            mStatusBar.setVisibility(View.GONE);
        }
    }

    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    //设置网络监听广播
    public void setBroadcast(NetworkConnectChangedReceiver.BroadcastCallBack broadcast){

        NetworkConnectChangedReceiver networkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        networkConnectChangedReceiver.setBroadcastCallBack(broadcast);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(networkConnectChangedReceiver,filter);

    }
    //获取用户信息
    public void WLoaignMake(){
        NToast.shortToastBaseApp(getString(R.string.需登陆));
    }

    //判断是否登陆
    public boolean isLogin(){

        if(CheckUtil.isEmpty(UserInfoManger.getInstance().getSessionkey())){
            return false;
        }

        return true;
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);//解除订阅
        if(null != unbinder){
            unbinder.unbind();
        }
        super.onDestroy();
    }

    protected void setStatusBar() {
//
//            // 设置状态栏底色颜色
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                getWindow().setStatusBarColor(color);
//            }else {
//
//            }
//
//        // 如果亮色，设置状态栏文字为黑色
//            if (isLightColor(color)) {
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            } else {
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (true) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    /**
     * 判断颜色是不是亮色
     *
     * @param color
     * @return
     * @from https://stackoverflow.com/questions/24260853/check-if-color-is-dark-or-light-in-android
     */
    private boolean isLightColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }

    /**
     * 获取StatusBar颜色，默认白色
     *
     * @return
     */
    protected @ColorInt int getStatusBarColor() {
        return Color.WHITE;
    }
}
