package fm.qian.michael.ui.fragment;


import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.RankMore;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;

import butterknife.OnClick;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.single.DownManger;

import static fm.qian.michael.utils.CommonUtils.getNumberIndex;

/*
 * lv   2018/9/11
 */
public class ComFragment extends BaseRecycleViewFragment {

    public static final int ONE = 0;//zhou
    public static final int TWO = 1;//
    public static final int THREE = 2;//
    public static final int FOUR = 3;//

    private int p = -1;
    private int p1 = -1;

    private int hight = -1;

    private int type;

    private String tid;
    private String day;

    public void setType(int type) {
        this.type = type;
    }

    private QuickAdapter quickAdapter;

    @OnClick({})
    public void onClick(View view) {
        switch (view.getId()) {

        }
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
        initView();
    }

    @Override
    public void loadData() {
        pageNo = 1;
        ranklist();
    }

    @Override
    public boolean isDamp() {
        return false;
    }

    private void initView(){
        getRvList().setLayoutManager(new LinearLayoutManager(getContext()));

        switch (type){
            case ONE:
                tid = GlobalVariable.ONE;
                day = "7";

                quickAdapter =  new QuickAdapter(R.layout.item_sel_voice){
                    @Override
                    protected void convert(BaseViewHolder helper, Object item) {
//
//                int p = DisplayUtils.getDimen(R.dimen.item_margin_four);
//
//                helper.itemView.setPadding(0,0,0,0);

//                helper.itemView.findViewById(R.id.haolou_layout).setPadding(p,0,p,0);

                        if(item instanceof ComAll){
                            ComAll rankMore = (ComAll) item;

                            if(DownManger.isDownloaded(rankMore.getUrl())){
                                helper.setGone(R.id.k_four,true);
                                helper.getView(R.id.k_four).setActivated(true);
                                helper.setText(R.id.item_down_tv,"已下载");
                            }else {
                                helper.setGone(R.id.k_four,false);
                            }

                            helper.setGone(R.id.item_tv_num,true);
                            helper.setText(R.id.item_tv_num, getNumberIndex(helper.getLayoutPosition()+1));

                            helper.setText(R.id.item_ming_tv,rankMore.getTitle());

                            if(CheckUtil.isEmpty(rankMore.getBroad())){
                                helper.setGone(R.id.k_one,false);
                            }else {
                                helper.setGone(R.id.k_one,true);
                                helper.setText(R.id.item_name_tv,rankMore.getBroad());
                            }
                            if(CheckUtil.isEmpty(rankMore.getPlaynums())){
                                helper.setGone(R.id.k_two,false);
                            }else {
                                helper.setGone(R.id.k_two,true);
                                helper.setText(R.id.item_peo_tv,rankMore.getPlaynums());
                            }
                            helper.setText(R.id.item_time_tv,rankMore.getMinute()+":"+rankMore.getSecond());



                            GlideUtil.setGlideImageMake(mFontext,rankMore.getCover_small(),
                                    (ImageView) helper.getView(R.id.head_portrait));

                        }

                    }
                };
                break;
            case TWO:
                tid = GlobalVariable.FOUR;
                day = "7";
                quickAdapter =  new QuickAdapter(R.layout.item_image_and_text_sp){
                    @Override
                    protected void convert(BaseViewHolder helper, Object item) {

//                        if(p < 0){
//                            p = DisplayUtils.getDimen(fm.qian.michael.R.dimen.item_margin_four);
//                            p1 = DisplayUtils.getDimen(fm.qian.michael.R.dimen.item_margin_two);
//                        }
//
//                        helper.itemView.setPadding(p,p1,p,p1);

                        helper.setGone(R.id.item_tv,false);

//                        if(hight < 0){
//                            hight = DisplayUtils.getDimen(fm.qian.michael.R.dimen.margin_120);
//                        }
//                        helper.itemView.setLayoutParams(LayoutParmsUtils.getGroupParms(hight));



                        if(item instanceof  ComAll){
                            ComAll rankMore = (ComAll) item;
                            GlideUtil.setGlideImageMake(mFontext,rankMore.getCover(),
                                    (ImageView) helper.getView(R.id.item_image));
                        }

                    }
                };
                break;
            case THREE:
                tid = GlobalVariable.ONE;
                day = "30";
                quickAdapter =  new QuickAdapter(R.layout.item_sel_voice){
                    @Override
                    protected void convert(BaseViewHolder helper, Object item) {
//
//                int p = DisplayUtils.getDimen(R.dimen.item_margin_four);
//
//                helper.itemView.setPadding(0,0,0,0);

//                helper.itemView.findViewById(R.id.haolou_layout).setPadding(p,0,p,0);

                        if(item instanceof  ComAll){
                            ComAll rankMore = (ComAll) item;
                            if(DownManger.isDownloaded(rankMore.getUrl())){
                                helper.setGone(R.id.k_four,true);
                                helper.getView(R.id.k_four).setActivated(true);
                                helper.setText(R.id.item_down_tv,"已下载");
                            }else {
                                helper.setGone(R.id.k_four,false);
                            }
                            helper.setGone(R.id.item_tv_num,true);
                            helper.setText(R.id.item_tv_num,  getNumberIndex(helper.getLayoutPosition()+1));

                            helper.setText(R.id.item_ming_tv,rankMore.getTitle());

                            if(CheckUtil.isEmpty(rankMore.getBroad())){
                                helper.setGone(R.id.k_one,false);
                            }else {
                                helper.setGone(R.id.k_one,true);
                                helper.setText(R.id.item_name_tv,rankMore.getBroad());
                            }
                            if(CheckUtil.isEmpty(rankMore.getPlaynums())){
                                helper.setGone(R.id.k_two,false);
                            }else {
                                helper.setGone(R.id.k_two,true);
                                helper.setText(R.id.item_peo_tv,rankMore.getPlaynums());
                            }
                            helper.setText(R.id.item_time_tv,rankMore.getMinute()+":"+rankMore.getSecond());



                            GlideUtil.setGlideImageMake(mFontext,rankMore.getCover_small(),
                                    (ImageView) helper.getView(R.id.head_portrait));

                        }
                    }
                };
                break;
            case FOUR:
                tid = GlobalVariable.FOUR;
                day = "30";
                quickAdapter =  new QuickAdapter(R.layout.item_image_and_text_sp){
                    @Override
                    protected void convert(BaseViewHolder helper, Object item) {

//                        if(p < 0){
//                            p = DisplayUtils.getDimen(fm.qian.michael.R.dimen.item_margin_four);
//                            p1 = DisplayUtils.getDimen(fm.qian.michael.R.dimen.item_margin_two);
//                        }
//
//                        helper.itemView.setPadding(p,p1,p,p1);

                        helper.setGone(R.id.item_tv,false);

//                        if(hight < 0){
//                            hight = DisplayUtils.getDimen(fm.qian.michael.R.dimen.margin_120);
//                        }
//
//                        helper.itemView.setLayoutParams(LayoutParmsUtils.getGroupParms(hight));

                        if(item instanceof  ComAll){
                            ComAll rankMore = (ComAll) item;
                            GlideUtil.setGlideImageMake(mFontext,rankMore.getCover(),
                                    (ImageView) helper.getView(R.id.item_image));
                        }
                    }
                };
                break;
        }

        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Object item = quickAdapter.getItem(position);

                if(item instanceof ComAll){
                    switch (type){
                        case ONE:
                        case THREE:
                            if(true){

                                CommonUtils.getIntent(mFontext, Arrays.asList((ComAll)item),0, GlobalVariable.FOUR);

                            }
                            break;
                        case TWO:
                        case FOUR:
                            if(true){
                                Intent intent = new Intent(mFontext, HeadGroupActivity.class);
                                intent.putExtra(HeadGroupActivity.HEADGROUP,((ComAll) item).getId());
                                startActivity(intent);
                            }
                            break;
                    }
                }

            }
        });

        getRvList().setAdapter(quickAdapter);
    }

    @Override
    public void Refresh() {
        pageNo = 1;
        ranklist();
    }

    @Override
    public void loadMore() {
        ranklist();
    }

    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynEvent(Event.PlayEvent event) {
        if(event.getI() == 1){

        }else if(event.getI() == 2){
            switch (type){
                case ONE:
                case THREE:
                    if(null != quickAdapter)
                        quickAdapter.notifyDataSetChanged();
                    break;
            }

        }

    }

    private void ranklist(){
        baseService.ranklist(tid, day, pageNo+"",new HttpCallback<List<ComAll>, BaseDataResponse<List<ComAll>>>() {
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
            public void onSuccess(List<ComAll> k) {

                if(isUpOrDown){
                    getRefreshLayout().finishLoadMore();
                    if(!CheckUtil.isEmpty(k)){
                        pageNo ++ ;
                        quickAdapter.addData(k);
                    }
                }else {
                    getRefreshLayout().finishRefresh();
                    if(!CheckUtil.isEmpty(k)){
                        pageNo ++ ;
                        quickAdapter.replaceData(k);
                    }else {
                        quickAdapter.replaceData(new ArrayList<>());
                        quickAdapter.setEmptyView(getEmpty("暂无数据"));
                    }
                }

            }
        },ComFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }



}
