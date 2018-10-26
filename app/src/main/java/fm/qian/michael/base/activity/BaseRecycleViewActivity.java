package fm.qian.michael.base.activity;


import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.utils.NetStateUtils;

/*
 * lv   2018/9/8   仅有 recycle  的下拉刷新
 */
public class BaseRecycleViewActivity extends BaseIntensifyActivity {

    public int pageNo = 1;
    public boolean isUpOrDown = false;//上拉下拉

    @OnClick({R.id.base_left_layout})
    public  void  onlick(View view){
        switch (view.getId()){
            case R.id.base_left_layout:
                finish();
                break;
        }
    }

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @Override
    public void initView() {
        super.initView();

        if(isDamp()){
            refreshLayout.setEnablePureScrollMode(true);
        }else {
            ClassicsHeader mClassicsHeader;
            int deta = new Random().nextInt(7 * 24 * 60 * 60 * 1000);
            mClassicsHeader = (ClassicsHeader)refreshLayout.getRefreshHeader();
            mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis()-deta));
            mClassicsHeader.setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
            // mClassicsHeader.setBackgroundResource(R.color.color_f3f);
            Drawable mDrawableProgress;
            mDrawableProgress = mClassicsHeader.getProgressView().getDrawable();
            if (mDrawableProgress instanceof LayerDrawable) {
                mDrawableProgress = ((LayerDrawable) mDrawableProgress).getDrawable(0);
            }
            mClassicsHeader.setEnableLastTime(false);//显示时间

            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(final RefreshLayout refreshlayout) {

                    if(!NetStateUtils.isNetworkConnected(BaseApplation.getBaseApp())){

                        NToast.shortToastBaseApp(BaseApplation.getBaseApp().getString(R.string.无网络));
                        refreshlayout.finishRefresh();
                        return;
                    }

                    isUpOrDown = false;//向下
                    Refresh();
                }
            });
            refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(RefreshLayout refreshlayout) {

                    if(!NetStateUtils.isNetworkConnected(BaseApplation.getBaseApp())){

                        NToast.shortToastBaseApp(BaseApplation.getBaseApp().getString(R.string.无网络));
                        refreshLayout.finishLoadMore();
                        return;
                    }

                    isUpOrDown = true;//向下
                    loadMore();
                }
            });
        }


    }

    public RecyclerView getRvList() {
        return rvList;
    }

    public SmartRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public void Refresh(){
        refreshLayout.finishRefresh();
    }
    public void loadMore(){
        refreshLayout.finishLoadMore();
    }

    public boolean isDamp(){
        return false;
    }


    public void onError(HttpException e){
        if(isUpOrDown){
            getRefreshLayout().finishLoadMore();
        }else {
            getRefreshLayout().finishRefresh();
        }
        NToast.shortToastBaseApp(e.getMsg());
    }

    public void onSuccess(){

    }
}
