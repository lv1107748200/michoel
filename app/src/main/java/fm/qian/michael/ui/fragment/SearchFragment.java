package fm.qian.michael.ui.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.common.BaseDownViewHolder;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.activity.WebParticularsActivity;
import fm.qian.michael.ui.activity.WebTBSParticularsActivity;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.SearchActivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.pop.CustomPopuWindConfig;
import fm.qian.michael.widget.pop.PopPlayListWindow;
import fm.qian.michael.widget.single.DownManger;

import static fm.qian.michael.utils.NetStateUtils.isWifi;

/*
 * lv   2018/9/25
 */
public class SearchFragment extends BaseRecycleViewFragment {

    public static final int ONE = 1;//zhou
    public static final int TWO = 2;//
    public static final int THREE = 3;//

    private int p = -1;
    private int p1 = -1;

    private int hight = -1;

    private String t, searchText;
    private boolean IdDown ;

    private List<ComAll> selList;

    private boolean isDown;

    private View footView;
    private PopPlayListWindow popPlayListWindow;

    public static SearchFragment getSearchFragment(){
      return   new SearchFragment();
    }

    private int type;
    public SearchFragment setType(int type) {
        this.type = type;
        return this;
    }

    private QuickAdapter quickAdapter;

    @BindView(R.id.buttom_layout)
    LinearLayout buttomLayout;
    @OnClick({R.id.add_layout,R.id.down_layout,R.id.play_layout})
    public void Onclick(View view){
        switch (view.getId()){
            case R.id.add_layout:

                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                isDown = false;
               addPlayerList();//仅仅添加到播单

                break;
            case R.id.down_layout://下载
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }

                isDown =true;
                if(isWifi(mFontext)){
                    addPlayerList();//底部点击下载 在 wifi
                }else {
                    setDelAlertDialog();
                }

                break;
            case R.id.play_layout://播放

                if(!CheckUtil.isEmpty(selList)){
                    CommonUtils.getIntent(mFontext,selList,0, GlobalVariable.THREE);
                }else {
                    NToast.shortToastBaseApp("请选择");
                }
                break;
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_search;
    }
    @Override
    public void init() {
        super.init();

        ini();
        initView();
    }

    public void load(){
        if(!CheckUtil.isEmpty(selList)){
            selList.clear();
            buttomLayout.setVisibility(View.GONE);
        }
        isUpOrDown = false;
        pageNo = 1;
        search();
    }

    @Override
    public void Refresh() {
        if(!CheckUtil.isEmpty(selList)){
            selList.clear();
            buttomLayout.setVisibility(View.GONE);
        }
        pageNo = 1;
        search();
    }

    @Override
    public void loadMore() {
        search();
    }

    private void initView(){
        RecyclerView.LayoutManager layoutManager = null;

        switch (type){
            case ONE:

                layoutManager =  new LinearLayoutManager(mFontext);
                quickAdapter =  new QuickAdapter(R.layout.item_image_and_text_sp,25){
                    @Override
                    protected void convert(BaseViewHolder helper, Object item) {

                        if(p < 0){
                            p = DisplayUtils.getDimen(R.dimen.item_margin_four);
                            p1 = DisplayUtils.getDimen(R.dimen.item_margin_two);
                        }

                        helper.itemView.setPadding(p,p1,p,p1);

                        helper.setGone(R.id.item_tv,false);

//                        if(hight < 0){
//                            hight = DisplayUtils.getDimen(R.dimen.margin_120);
//                        }
//
//                        helper.itemView.setLayoutParams(LayoutParmsUtils.getGroupParms(hight));

                        if(item instanceof ComAll){
                            ComAll comAll = (ComAll) item;
                            GlideUtil.setGlideImageMake(mFontext,comAll.getCover(),
                                    (ImageView) helper.getView(R.id.item_image));
                        }

                    }
                };
                break;
            case TWO:
                selList = new ArrayList<>();

                ((SimpleItemAnimator) getRvList().getItemAnimator()).setSupportsChangeAnimations(false);

                layoutManager =  new LinearLayoutManager(mFontext);
                quickAdapter =  new QuickAdapter(R.layout.item_sel_voice,25){
                    @Override
                    protected void convert(BaseViewHolder helper, Object item) {
//
//                int p = DisplayUtils.getDimen(R.dimen.item_margin_four);
//
//                helper.itemView.setPadding(0,0,0,0);

//                helper.itemView.findViewById(R.id.haolou_layout).setPadding(p,0,p,0);

                        if(item instanceof  ComAll){
                            ComAll rankMore = (ComAll) item;

                            helper.setGone(R.id.sel_image,true);

                            String path;
                            int id;

                            if(CheckUtil.isEmpty(rankMore.getDownPath())){
                                path = DownManger.createPath(rankMore.getUrl());
                                id = FileDownloadUtils.generateId(rankMore.getUrl(), path);
                                rankMore.setDownPath(path);
                                rankMore.setIsDown(id);

                            }else {

                                path = rankMore.getDownPath();
                                id = rankMore.getIsDown();

                                //  NLog.e(NLog.TAGDOWN," 视图 下载id : " + id);
                            }

                            DownManger.updateViewHolder(id,new BaseDownViewHolder(id,helper.getLayoutPosition(),helper.getView(R.id.k_four),
                                    (TextView) helper.getView(R.id.item_down_tv)));

                                int statue = DownManger.isDownStatus(id,path);

                                if(statue == FileDownloadStatus.completed){

                                    helper.setGone(R.id.k_four,true);
                                    helper.getView(R.id.k_four).setActivated(true);
                                    helper.setText(R.id.item_down_tv,getString(R.string.已下载));

                                }else if(statue == FileDownloadStatus.progress||statue == FileDownloadStatus.started || statue == FileDownloadStatus.connected ){

                                    helper.setGone(R.id.k_four,true);
                                    helper.getView(R.id.k_four).setActivated(false);
                                    helper.setText(R.id.item_down_tv,getString(R.string.下载中));

                                }else {

                                    helper.setGone(R.id.k_four,false);

                                }


                            if(issetSelList(rankMore)){
                                helper.itemView.setSelected(true);
                            }else {
                                helper.itemView.setSelected(false);
                            }

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
//                    @Override
//                    public void onViewRecycled(@NonNull BaseViewHolder holder) {
//                        super.onViewRecycled(holder);
//                        // NLog.e(NLog.TAGDOWN," 视图 onViewRecycled : " + holder.getLayoutPosition()+1);
//
//                        if(null != holder){
//                            Object item = quickAdapter.getItem(holder.getLayoutPosition());
//                            if(item instanceof ComAll){
//                                ComAll rankMore = (ComAll) item;
//                                String path ;
//                                int id ;
//                                //  NLog.e(NLog.TAGDOWN," 视图 下载id : " + id);
//
//                                if(CheckUtil.isEmpty(rankMore.getDownPath())){
//                                    path = DownManger.createPath(rankMore.getUrl());
//                                    id = FileDownloadUtils.generateId(rankMore.getUrl(), path);
//                                }else {
//                                    id =  rankMore.getIsDown();
//                                }
//
//                                DownManger.updateViewHolder(id);
//                            }
//                        }
//                    }
                };
                DownManger.setQAdapter(quickAdapter);

                break;
            case THREE:
                layoutManager =  new GridLayoutManager(mFontext,2);
                quickAdapter =  new QuickAdapter(R.layout.item_image_and_text_changed_one,25){
                    @Override
                    protected void convert(BaseViewHolder helper, Object item) {

                        if(p < 0){
                            p = DisplayUtils.getDimen(R.dimen.item_margin_four);
                            p1 = DisplayUtils.getDimen(R.dimen.item_margin_two);
                        }

                        helper.itemView.setPadding(p,p1,p,p1);

                        if(hight < 0){
                            hight = wight2();
                        }

                        LayoutParmsUtils.setHight(hight,helper.getView(R.id.item_image));

                        if(item instanceof  ComAll){
                            helper.setText(R.id.item_tv,((ComAll) item).getTitle());
                            GlideUtil.setGlideImageMake(helper.itemView.getContext(),((ComAll) item).getCover(),
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
                    ComAll rankMore = (ComAll) item;
                    switch (type){
                        case ONE:
                            if(true){
                                Intent intent = new Intent();
                                intent.setClass(mFontext, HeadGroupActivity.class);
                                intent.putExtra(HeadGroupActivity.HEADGROUP,rankMore.getId());
                                startActivity(intent);
                            }
                            break;
                        case TWO:

                            view.setSelected(!view.isSelected());
                            setSellList(rankMore);

                            break;
                        case THREE:
                            if(true){
                                CommonUtils.getWebIntent(mFontext,GlobalVariable.TWO,rankMore.getId());
                            }
                            break;
                    }

                }

            }
        });
        getRvList().setLayoutManager(layoutManager);
        getRvList().setAdapter(quickAdapter);
    }

    private int wight2(){
        int  wight = DisplayUtils.getWideP(2,0
                ,DisplayUtils.getDimen(R.dimen.margin_80));

        // NLog.e(NLog.TAGOther,"宽度： --->" + wight);
        return wight;
    }
    private void ini(){
        pageNo = 1;
        p = -1;
        p1 = -1;
        hight = -1;
    }


    public void set(String searchText){
        this.searchText = searchText;
    }


    private void search(){

        baseService.search(type+"", searchText, pageNo+"",
                new HttpCallback<List<ComAll>,
                BaseDataResponse<List<ComAll>>>() {
            @Override
            public void onError(HttpException e) {
                if(isUpOrDown){
                    if(type ==  TWO){
                        buttomLayout.setVisibility(View.VISIBLE);
                    }
                    getRefreshLayout().finishLoadMore();
                }else {
                    getRefreshLayout().finishRefresh();
                }
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(List<ComAll> comAlls) {
                if(isUpOrDown){
                    getRefreshLayout().finishLoadMore();
                    if(!CheckUtil.isEmpty(comAlls)){
                        pageNo ++ ;
                        quickAdapter.addData(comAlls);
                    }
                }else {
                    getRefreshLayout().finishRefresh();
                    if(!CheckUtil.isEmpty(comAlls)){
                        pageNo ++ ;
                        quickAdapter.replaceData(comAlls);
                    }else {
                        quickAdapter.replaceData(new ArrayList<>());
                        quickAdapter.setEmptyView(getEmpty());
                    }
                }
            }
        },SearchFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));

    }


    //检索 id 转换
    private void getIdDown(List<ComAll> comAlls){

        if(type == TWO){
            IdDown = false;

            if(isUpOrDown){
                if(!CheckUtil.isEmpty(comAlls)){
                    DownManger.setIdDown(comAlls, new DownManger.ResultCallback<List<ComAll>>() {
                        @Override
                        public void onSuccess(List<ComAll> o) {
                            if(!CheckUtil.isEmpty(o)){
                                IdDown = true;
                                    getRefreshLayout().finishLoadMore();
                                    if(!CheckUtil.isEmpty(o)){
                                        pageNo ++ ;
                                        quickAdapter.addData(o);
                                    }
                                quickAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(String errString) {

                        }
                    });
                }else {
                    getRefreshLayout().finishLoadMore();
                    NToast.shortToastBaseApp("暂无更多数据");
                }



            }else {
                if(CheckUtil.isEmpty(comAlls)){
                    getRefreshLayout().finishRefresh();
                    quickAdapter.replaceData(new ArrayList<>());
                    quickAdapter.setEmptyView(getEmpty());
                }else {
                    DownManger.setIdDown(comAlls, new DownManger.ResultCallback<List<ComAll>>() {
                        @Override
                        public void onSuccess(List<ComAll> o) {
                            if(!CheckUtil.isEmpty(o)){
                                IdDown = true;
                                    getRefreshLayout().finishRefresh();
                                    if(!CheckUtil.isEmpty(o)){
                                        pageNo ++ ;
                                        quickAdapter.replaceData(o);
                                    }
                                quickAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(String errString) {

                        }
                    });
                }
            }


        }else {
            if(isUpOrDown){
                getRefreshLayout().finishLoadMore();
                if(!CheckUtil.isEmpty(comAlls)){
                    pageNo ++ ;
                    quickAdapter.addData(comAlls);
                }
            }else {
                getRefreshLayout().finishRefresh();
                if(!CheckUtil.isEmpty(comAlls)){
                    pageNo ++ ;
                    quickAdapter.replaceData(comAlls);
                }else {
                    quickAdapter.replaceData(new ArrayList<>());
                    quickAdapter.setEmptyView(getEmpty());
                }
            }
        }



    }



    private boolean issetSelList(ComAll comAll){


        for(int i = 0,j=selList.size();i<j;i++ ){
            if(comAll.getId().equals(selList.get(i).getId())){
                return true;
            }
        }

        return false;
    }

    private void setSellList(ComAll comAll){

        for(int i = 0,j=selList.size();i<j;i++ ){
            if(comAll.getId().equals(selList.get(i).getId())){
                selList.remove(i);

                if(CheckUtil.isEmpty(selList)){
                    buttomLayout.setVisibility(View.GONE);
                }
                return ;
            }
        }

        selList.add(comAll);


        if(buttomLayout.getVisibility() == View.GONE){
            buttomLayout.setVisibility(View.VISIBLE);
        }

    }

    private View getFootView(){

        if(null == footView){
            footView = LayoutInflater.from(mFontext).inflate(R.layout.foot_make,null,false);
        }

        return footView;
    }

    //添加波胆
    private void addPlayerList(){

        if(null == popPlayListWindow){
            popPlayListWindow = new PopPlayListWindow(this,new CustomPopuWindConfig.Builder(mFontext)
                    .setOutSideTouchable(true)
                    .setFocusable(true)
                    .setAnimation(R.style.popup_hint_anim)
                    .setWith((com.hr.bclibrary.utils.DisplayUtils.getScreenWidth(mFontext) - com.hr.bclibrary.utils.DisplayUtils.dip2px(mFontext,80)))
                    .build());
            popPlayListWindow.setPopPlayListWindowCallBack(new PopPlayListWindow.PopPlayListWindowCallBack() {
                @Override
                public List<ComAll> getSelComAll() {
                    return selList;
                }

                @Override
                public void state(int what) {
                    if(isDown){
                        down(selList);
                    }

                }
            });
        }else {

        }
        popPlayListWindow.user_broadcastall();
        popPlayListWindow.show(buttomLayout);
    }

    //非wifi网络下的提醒
    private void setDelAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mFontext);
        builder.setTitle("提示");
        builder.setMessage("当前非WiFi网络是否确定下载？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                addPlayerList();//下载 非WiFi环境

            }

        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    //下载管理
    private void down(List<ComAll> list){
        if(!CheckUtil.isEmpty(list)){
            DownManger.setIdAndPath(list,null,new DownManger.ResultCallback() {
                @Override
                public void onSuccess(Object baseDownloadTaskSparseArray) {
                    if(null != quickAdapter){
                        quickAdapter.notifyDataSetChanged();
                        NToast.shortToastBaseApp(getString(R.string.成功添加下载任务));
                    }
                }
                @Override
                public void onError(String errString) {

                }
            });
        }else {
            NToast.shortToastBaseApp(getString(R.string.请选择));
        }
    }
}
