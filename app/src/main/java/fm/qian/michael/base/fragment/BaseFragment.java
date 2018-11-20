package fm.qian.michael.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.xxbm.sbecomlibrary.utils.DisplayUtils;

import org.greenrobot.eventbus.EventBus;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.base.activity.BaseActivity;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.utils.NetStateUtils;
import fm.qian.michael.widget.dialog.LoadingDialog;
import fm.qian.michael.widget.pop.CustomPopuWindConfig;
import fm.qian.michael.widget.pop.PopLoginWindow;
import fm.qian.michael.widget.single.UserInfoManger;

/**
 * Created by 吕 on 2017/10/26.  通用版
 */

public class BaseFragment extends com.xxbm.sbecomlibrary.base.fragment.BaseFragment{
    FrameLayout head_layout;

    private PopLoginWindow popLoginWindow;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if(addHead() != null){
            head_layout = baseFgmView.findViewById(R.id.head_layout);
            head_layout.addView(addHead());
        }
        return baseFgmView;
    }
    //为ScrollView 内部添加 布局
    public View addHead(){

        return null;
    }

    protected  void initHeader(LayoutInflater inflater){

    }

    public  void initWidget(View view){
        init();
    }

    public void init(){

    }
    public void loadData(){

    }
    public void stopLoad(){

    }
    public void everyTime(boolean isVisibleToUser){

    }
    public View getEmpty(String message){
        View view = LayoutInflater.from(mFontext).inflate(R.layout.empty_view,null,false);

        TextView textView = view.findViewById(R.id.emty_tv);
        if(!CheckUtil.isEmpty(message)){
            textView.setText(message);
        }
        return view;
    }
    public View getError(String message){
        View view = LayoutInflater.from(mFontext).inflate(R.layout.error_net_view,null,false);

        TextView textView = view.findViewById(R.id.error_tv);
        if(!CheckUtil.isEmpty(message)){
            textView.setText(message);
        }
        return view;
    }
    public void WLoaignMake(){
        if(popLoginWindow == null){
            popLoginWindow = new PopLoginWindow(new CustomPopuWindConfig.Builder(mFontext)
                    .setOutSideTouchable(false)
                    .setFocusable(true)
                    .setAnimation(R.style.popup_hint_anim)
                    .setWith((DisplayUtils.getScreenWidth(mFontext) - DisplayUtils.dip2px(mFontext,80)))
                    .build(),mFontext);
            popLoginWindow.show(baseFgmView);
        }else {
            popLoginWindow.show(baseFgmView);
        }
        NToast.shortToastBaseApp(getString(R.string.需登陆));
    }
    public void WLoaignMake(boolean isf){
        if(popLoginWindow == null){
            popLoginWindow = new PopLoginWindow(new CustomPopuWindConfig.Builder(mFontext)
                    .setOutSideTouchable(false)
                    .setFocusable(true)
                    .setAnimation(R.style.popup_hint_anim)
                    .setWith((DisplayUtils.getScreenWidth(mFontext) - DisplayUtils.dip2px(mFontext,80)))
                    .build(),mFontext);
            popLoginWindow.show(baseFgmView);
        }else {
            popLoginWindow.show(baseFgmView);
        }
    }

    //判断是否登陆
    public boolean isLogin(){

        if(CheckUtil.isEmpty(UserInfoManger.getInstance().getSessionkey())){
            return false;
        }

        return true;
    }
    //判断网络是否连接
    public boolean isEableNet(){

        boolean isNet = NetStateUtils.isNetworkConnected(mFontext);

        if(isNet){

        }else {
            NToast.shortToastBaseApp(getString(R.string.无网络));
        }

        return isNet;
    }

    public void showLoadingDialog(String message){

        if(null == loadingDialog){
            loadingDialog = new LoadingDialog(mFontext);
        }

        loadingDialog.show(message);

    }

    public void dissLoadingDialog(){
        if(null != loadingDialog)
        loadingDialog.diss();
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
