package fm.qian.michael.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.xxbm.sbecomlibrary.utils.NLog;
import com.trello.rxlifecycle2.android.FragmentEvent;

import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseFragment;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.Video;
import com.xxbm.sbecomlibrary.net.entry.response.Base;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import com.xxbm.sbecomlibrary.net.http.HttpCallback;
import com.xxbm.sbecomlibrary.net.http.HttpException;
import fm.qian.michael.ui.activity.WebParticularsActivity;
import fm.qian.michael.ui.activity.WebTBSParticularsActivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.qian.michael.ui.activity.WebParticularsActivity;
import fm.qian.michael.utils.DisplayUtils;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.widget.custom.SearchLayout;

/**
 * Created by 吕 on 2018/1/2.
 * 文章
 */

public class ArticleFragment extends BaseRecycleViewFragment implements
        SearchLayout.SearchCallBack {

    private QuickAdapter quickAdapter;
    private String tagText;

    @BindView(R.id.id_search)
    SearchLayout idSearch;

    @OnClick({})
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_article;
    }

    @Override
    public void initWidget(View view) {
        super.initWidget(view);

    }

    @Override
    public void init() {
        super.init();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        idSearch.setSearchCallBack(this);
        idSearch.setNv(View.GONE);

        getRvList().setLayoutManager(new LinearLayoutManager(getContext()));

        quickAdapter = new QuickAdapter(R.layout.item_text_layout){
            @Override
            protected void convert(BaseViewHolder helper, Object item) {

//                if(helper.getLayoutPosition() == 5){
//                    helper.itemView .setBackgroundResource(R.color.color_F86);
//                }

//                int p = DisplayUtils.getDimen(R.dimen.item_margin_four);
//
//                helper.itemView.setPadding(0,0,0,0);
//
//                helper.itemView.findViewById(R.id.haolou_layout).setPadding(p,0,p,0);

                if(item instanceof  Base){

                    Base itemBase = (Base) item;

                    helper.setText(R.id.title_tv_news,itemBase.getTitle());
                    helper.setText(R.id.title_tv_date,itemBase.getPubdate());

                    GlideUtil.setGlideImageMake(helper.itemView.getContext(),itemBase.getCover(),
                            (ImageView) helper.getView(R.id.item_image));
                }

            }
        };
        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {



                Object item = quickAdapter.getItem(position);

                if(item instanceof  Base) {
                    Base itemBase = (Base) item;
                    CommonUtils.getWebIntent(mFontext,GlobalVariable.THREE,itemBase.getId());
                }
            }
        });
        getRvList().setAdapter(quickAdapter);
    }

    @Override
    public void loadData() {
        super.loadData();
        pageNo = 1;
        tagText = "";
        articlelist(tagText,pageNo+"");

        search_hotwords();
    }
    @Override
    public void textCallBack(String text) {

        idSearch.setNv(View.GONE);
        getRefreshLayout().setVisibility(View.VISIBLE);

        isUpOrDown = false;
        pageNo = 1;
        tagText = text;
        articlelist(tagText+"",pageNo+"");

    }

    @Override
    public void show(int i) {
        getRefreshLayout().setVisibility(i);

        if(null != idSearch){
            if(CheckUtil.isEmpty(idSearch.getStringList())){
                search_hotwords();
            }
        }
    }
    @Override
    public void del(int num) {
        idSearch.setNv(View.GONE);
        getRefreshLayout().setVisibility(View.VISIBLE);
        if(num == 1){
            pageNo = 1;
            tagText = "";
            getRefreshLayout().autoRefresh();
        }
    }

    @Override
    public void scan() {

    }
    @Override
    public void Refresh() {
        if(null != idSearch){
            if(CheckUtil.isEmpty(idSearch.getStringList())){
                search_hotwords();
            }
        }
        pageNo = 1;
        articlelist(tagText+"",pageNo+"");
    }

    @Override
    public void loadMore() {

        articlelist(tagText+"",pageNo+"");
    }

    private void articlelist(String q, String p){
        baseService.articlelist(q, p, new HttpCallback<List<Base>, BaseDataResponse<List<Base>>>() {
            @Override
            public void onNotNet() {
                if(isUpOrDown){
                    getRefreshLayout().finishLoadMore();
                }else {
                    getRefreshLayout().finishRefresh();
                }
                if(CheckUtil.isEmpty(quickAdapter.getData())){
                    quickAdapter.setEmptyView(getError(""));
                }else {
                    super.onNotNet();
                }

            }

            @Override
            public void onError(HttpException e) {

                if(isUpOrDown){
                    getRefreshLayout().finishLoadMore();
                }else {
                    getRefreshLayout().finishRefresh();
                }
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(List<Base> baseList) {

                if(isUpOrDown){
                    getRefreshLayout().finishLoadMore();
                    if(!CheckUtil.isEmpty(baseList)){
                        pageNo ++ ;
                        quickAdapter.addData(baseList);
                    }
                }else {
                    getRefreshLayout().finishRefresh();
                    if(!CheckUtil.isEmpty(baseList)){
                        pageNo ++ ;
                        quickAdapter.replaceData(baseList);
                    }else {
                        quickAdapter.replaceData(new ArrayList<>());
                        quickAdapter.setEmptyView(getEmpty(getString(R.string.empty)+getString(R.string.emptyone)));
                    }
                }

            }
        },ArticleFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }

    private void search_hotwords(){

        baseService.search_hotwords("2", new HttpCallback<List<Base>, BaseDataResponse<List<Base>>>() {
            @Override
            public void onNotNet() {
                //super.onNotNet();
            }

            @Override
            public void onError(HttpException e) {

            }

            @Override
            public void onSuccess(List<Base> baseList) {

                if(!CheckUtil.isEmpty(baseList)){
                    idSearch.setId_flowlayout(baseList);
                }else {

                }
            }
        },ArticleFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));

    }
    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynEvent(Event.NetEvent event) {
        if(event.getI() == 1){
            if(null != quickAdapter){
                if(CheckUtil.isEmpty(quickAdapter.getData())){
                    getRefreshLayout().autoRefresh();
                }else {
                    if(null != idSearch){
                        if(CheckUtil.isEmpty(idSearch.getStringList())){
                            search_hotwords();
                        }
                    }
                }
            }else {
                if(null != idSearch){
                    if(CheckUtil.isEmpty(idSearch.getStringList())){
                        search_hotwords();
                    }
                }
            }

        }else if(event.getI() == 2){

        }
    }
}
