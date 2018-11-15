package fm.qian.michael.base.fragment;


import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import fm.qian.michael.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.utils.NetStateUtils;

/*
 * lv   2018/9/7  下拉刷新标准版
 */
public class BaseRecycleViewFragment extends BaseFragment {

    public int pageNo = 1;
    public boolean isUpOrDown = false;//上拉下拉

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_recycle;
    }

    @Override
    public void init() {
        super.init();
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
            ClassicsFooter classicsFooter = (ClassicsFooter) refreshLayout.getRefreshFooter();
            classicsFooter.setFinishDuration(300);
            if(!isEnableLoadMore()){
                refreshLayout.setEnableLoadMore(false);
            }

            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(final RefreshLayout refreshlayout) {

//                    if(!NetStateUtils.isNetworkConnected(BaseApplation.getBaseApp())){
//
//                        NToast.shortToastBaseApp(BaseApplation.getBaseApp().getString(R.string.无网络));
//                        refreshlayout.finishRefresh();
//                        return;
//                    }

                     isUpOrDown = false;//向下

                    if(isJYLogin()){
                        if(isLogin()){
                            Refresh();
                        }else {
                            refreshlayout.finishRefresh();
                        }
                    }else {
                        Refresh();
                    }



                }
            });
            refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(RefreshLayout refreshLayout) {

//                    if(!NetStateUtils.isNetworkConnected(BaseApplation.getBaseApp())){
//
//                        NToast.shortToastBaseApp(BaseApplation.getBaseApp().getString(R.string.无网络));
//                        refreshLayout.finishLoadMore();
//                        return;
//                    }

                    isUpOrDown = true;

                    if(isJYLogin()){
                        if(isLogin()){
                            loadMore();
                        }else {
                            refreshLayout.finishLoadMore();
                        }
                    }else {
                        loadMore();
                    }
                }
            });

        }


        setRvList();
    }

    @Override
    public void loadData() {
        super.loadData();
    }


    public RecyclerView getRvList() {
        return rvList;
    }

    public SmartRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public void setRvList(){

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
    public boolean isEnableLoadMore(){
        return true;
    }
    public boolean isJYLogin(){
        return false;
    }
}
