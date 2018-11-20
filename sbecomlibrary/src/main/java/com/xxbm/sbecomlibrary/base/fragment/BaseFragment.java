package com.xxbm.sbecomlibrary.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.xxbm.sbecomlibrary.base.BaseApplation;
import com.xxbm.sbecomlibrary.base.activity.BaseActivity;
import com.xxbm.sbecomlibrary.net.base.BaseService;

import org.greenrobot.eventbus.EventBus;


import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by 吕 on 2017/10/26.  通用版
 */

public class BaseFragment extends AbstractBaseFragment{
    //Fragment的View加载完毕的标记
    private boolean isViewCreated = false;

    //Fragment对用户可见的标记
    private boolean isLoad = false;

    public BaseActivity mFontext;

    @Inject
    public BaseService baseService;
    public View baseFgmView;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFontext = (BaseActivity) getActivity();
        BaseApplation.getBaseApp().getAppComponent().inject(this);
        baseFgmView = inflater.inflate(getContentViewId(), container, false);


        unbinder =  ButterKnife.bind(this, baseFgmView);


        initHeader(inflater);
        initWidget(baseFgmView);
        return baseFgmView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isViewCreated = true;
        lazyLoad();
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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        lazyLoad();
        everyTime(isVisibleToUser);
    }


    private void lazyLoad() {
        if(!isViewCreated)
            return;
        if (getUserVisibleHint()) {
            if(!isLoad){
                loadData();
                isLoad = true;
            }
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }
    public void loadData(){

    }
    public void stopLoad(){

    }
    public void everyTime(boolean isVisibleToUser){

    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(null != unbinder){
            unbinder.unbind();
        }
        EventBus.getDefault().unregister(this);//解除订阅
        isViewCreated = false;
        isLoad = false;
    }

}
