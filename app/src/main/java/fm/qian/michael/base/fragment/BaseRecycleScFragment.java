package fm.qian.michael.base.fragment;


import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import fm.qian.michael.R;

/*
 * lv   2018/9/7  scrollview 的嵌套的
 */
public class BaseRecycleScFragment extends BaseFragment {

    public int pageNo = 1;
    public boolean isUpOrDown = true;//上拉下拉

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_recycle_s;
    }
    @Override
    public void init() {
        super.init();
        rvList.setNestedScrollingEnabled(false);

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
                      isUpOrDown = false;//向下
                    Refresh();
                }
            });
            refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(RefreshLayout refreshLayout) {
                    isUpOrDown = true;//向下
                    loadMore();
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

    public RefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public void setRvList(){

    }
    public void Refresh(){

    }
    public void loadMore(){

    }

    public boolean isDamp(){
        return false;
    }
}
