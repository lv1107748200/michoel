package fm.qian.michael.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.hr.bclibrary.utils.DisplayUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseFragment;
import fm.qian.michael.base.fragment.BaseRecycleScFragment;
import fm.qian.michael.common.BaseDownViewHolder;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.entity.DownStaus;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.db.UseData;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.base.BaseResponse;
import fm.qian.michael.net.entry.response.Album;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.UserInfo;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.service.MqService;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.activity.MainActivity;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.ui.activity.dim.SearchActivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.utils.SPUtils;
import fm.qian.michael.widget.custom.SelectableRoundedImageView;
import fm.qian.michael.widget.manger.ScrollLinearLayoutManager;
import fm.qian.michael.widget.pop.CustomPopuWindConfig;
import fm.qian.michael.widget.pop.PopPlayListWindow;
import fm.qian.michael.widget.pop.PopShareWindow;
import fm.qian.michael.widget.single.DownManger;
import fm.qian.michael.widget.single.UserInfoManger;
import fm.qian.michael.widget.th.ThreadPoolProxyFactory;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static fm.qian.michael.common.UserInforConfig.USERMUSICID;
import static fm.qian.michael.common.UserInforConfig.USERMUSICNAME;
import static fm.qian.michael.common.UserInforConfig.USERMUSICTYPE;

/*
 * lv   2018/9/19  音频专辑
 */
public class GroupVoiseFragment extends BaseFragment implements View.OnClickListener{

    public int pageNo = 1;
    public boolean isUpOrDown = false;//上拉下拉

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.buttom_layout)
    LinearLayout buttomLayout;


    private PopPlayListWindow popPlayListWindow;
    private PopShareWindow popShareWindow;

    public static final String HEADGROUP = "HEADGROUP";
    private String id;
    private QuickAdapter quickAdapter;//list
    private Album  album;

    private List<ComAll> comAllList;
    private List<ComAll> selList;

    private View headView;


    private boolean isSelMore = false;//是否多选操作

    private boolean isPlay = false;

    private ComAll comAll;

    TextView itemTv;
    SelectableRoundedImageView itemImage;

    LinearLayout layout_fav,layout_down,
            layout_share,layout_select,
            layout_orderdesc,sel_all_Layout,
            cancel_layout,xq_layout,gs_layout,xq_gs_layout;
    RelativeLayout relayout_sel_cancel;

    @OnClick({R.id.add_layout,R.id.down_layout,R.id.play_layout})
    public void Onclick(View view){
        switch (view.getId()){
            case R.id.add_layout://添加到播单
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                if(isPay()){//添加到播单
                    return;
                }

                if(null == popPlayListWindow){
                    popPlayListWindow = new PopPlayListWindow(this,new CustomPopuWindConfig.Builder(mFontext)
                            .setOutSideTouchable(true)
                            .setFocusable(true)
                            .setAnimation(R.style.popup_hint_anim)
                            .setWith((DisplayUtils.getScreenWidth(mFontext) - DisplayUtils.dip2px(mFontext,80)))
                            .build());
                    popPlayListWindow.setPopPlayListWindowCallBack(new PopPlayListWindow.PopPlayListWindowCallBack() {
                        @Override
                        public List<ComAll> getSelComAll() {
                            return selList;
                        }
                    });
                }else {

                }
                popPlayListWindow.user_broadcastall();
                popPlayListWindow.show(view);
                break;
            case R.id.down_layout://下载
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                if(isPay()){//下载
                    return;
                }
                if(!CheckUtil.isEmpty(selList)){
                    DownManger.setIdAndPath(selList,null,new DownManger.ResultCallback() {
                        @Override
                        public void onSuccess(Object baseDownloadTaskSparseArray) {
                            if(null != quickAdapter){
                                quickAdapter.notifyDataSetChanged();
                                NToast.shortToastBaseApp("成功添加任务");
                            }
                        }
                        @Override
                        public void onError(String errString) {

                        }
                    });
                }else {
                    NToast.shortToastBaseApp("请选择");
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
    public void onClick(View view){
        switch (view.getId()){
            case R.id.layout_fav://收藏
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                if(isPay()){//收藏
                    return;
                }
                if(view.isSelected()){//取消收藏
                    user_favorite("del");
                }else {//收藏
                    user_favorite("add");
                }


                break;
            case R.id.layout_down://下载

                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                if(isPay()){//下载
                    return;
                }
                if(isSelMore)
                    return;
                if(!CheckUtil.isEmpty(comAllList)){
                    DownManger.setIdAndPath(comAllList,null,
                            new DownManger.ResultCallback() {

                        @Override
                        public void onSuccess(Object baseDownloadTaskSparseArray) {
                            if(null != quickAdapter){
                                quickAdapter.notifyDataSetChanged();
                                NToast.shortToastBaseApp("成功添加任务");
                            }

                        }

                        @Override
                        public void onError(String errString) {

                        }
                    });
                }
                break;
            case R.id.layout_share://分享
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }

                if(isPay()){//分享
                    return;
                }

                if(GlobalVariable.ONE.equals(album.getIspay())){ // 0不是  1是
                    NToast.shortToastBaseApp("付费专辑不能分享");
                    return;
                }

                if(null == popShareWindow){
                    popShareWindow = new PopShareWindow(mFontext,new CustomPopuWindConfig.Builder(mFontext)
                            .setOutSideTouchable(true)
                            .setFocusable(true)
                            .setTouMing(true)
                            .setAnimation(R.style.popup_hint_anim)
                            .setWith((DisplayUtils.getScreenWidth(mFontext)))
                            .build());
                    popShareWindow.setShareData(new PopShareWindow.ShareData(album.getTitle(),album.getCover(),album.getBrief(),album.getShareurl()));
                    popShareWindow.show(view);
                }else {
                    popShareWindow.setShareData(new PopShareWindow.ShareData(album.getTitle(),album.getCover(),album.getBrief(),album.getShareurl()));
                    popShareWindow.show(view);
                }

                break;
            case R.id.layout_select://多选

                isSelMore = !isSelMore;

                if(isSelMore){
                    relayout_sel_cancel.setVisibility(View.VISIBLE);
                }else {
                    //buttomLayout.setVisibility(View.GONE);
                }

                quickAdapter.notifyDataSetChanged();

                break;
            case R.id.layout_orderdesc://排序

                setpaixv();
                break;
            case R.id.sel_all_Layout://选中所有

                sel_all_Layout.setSelected(!sel_all_Layout.isSelected());

                if(sel_all_Layout.isSelected()){
                    if(null != selList){
                        selList.clear();
                        selList.addAll(comAllList);
                        buttomLayout.setVisibility(View.VISIBLE);
                    }
                }else {
                    if(!CheckUtil.isEmpty(selList)){
                        selList.clear();
                        buttomLayout.setVisibility(View.GONE);
                    }
                }
                quickAdapter.notifyDataSetChanged();

                break;
            case R.id.cancel_layout://取消所有

                isSelMore = !isSelMore;

                buttomLayout.setVisibility(View.GONE);
                relayout_sel_cancel.setVisibility(View.GONE);

                if(!CheckUtil.isEmpty(selList)) {
                    selList.clear();
                }

                quickAdapter.notifyDataSetChanged();

                break;
            case R.id.xq_layout:
                setLayout(0);
                break;
            case R.id.gs_layout:
                setLayout(1);
                break;
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_group_voise;
    }

    @Override
    public void init() {
        super.init();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        selList = new ArrayList<>();

        Intent intent = mFontext.getIntent();
        id = intent.getStringExtra(HEADGROUP);


         headView = LayoutInflater.from(mFontext).inflate(R.layout.head_group_voise,null,false);
        itemTv = headView.findViewById(R.id.item_tv);
        itemImage = headView.findViewById(R.id.item_image);
        relayout_sel_cancel = headView.findViewById(R.id.relayout_sel_cancel);

        layout_fav =   headView.findViewById(R.id.layout_fav);
        layout_down =   headView.findViewById(R.id.layout_down);
        layout_share =   headView.findViewById(R.id.layout_share);
        layout_select =   headView.findViewById(R.id.layout_select);
        layout_orderdesc =   headView.findViewById(R.id.layout_orderdesc);
        sel_all_Layout =   headView.findViewById(R.id.sel_all_Layout);
        cancel_layout =   headView.findViewById(R.id.cancel_layout);
        xq_layout = headView.findViewById(R.id.xq_layout);
        gs_layout = headView.findViewById(R.id.gs_layout);
        xq_gs_layout = headView.findViewById(R.id.xq_gs_layout);


        layout_fav.setOnClickListener(this);
        layout_down.setOnClickListener(this);
        layout_share.setOnClickListener(this);
        layout_select.setOnClickListener(this);
        layout_orderdesc.setOnClickListener(this);
        sel_all_Layout.setOnClickListener(this);
        cancel_layout.setOnClickListener(this);
        relayout_sel_cancel.setOnClickListener(this);
        xq_layout.setOnClickListener(this);
        gs_layout.setOnClickListener(this);


        rvList.setLayoutManager(new LinearLayoutManager(mFontext));

        quickAdapter =  new QuickAdapter(R.layout.item_sel_voice){

            @Override
            protected void convert(BaseViewHolder helper, Object item) {

                if(item instanceof  ComAll){
                    ComAll rankMore = (ComAll) item;

                    String path = DownManger.createPath(rankMore.getUrl());
                    int id = FileDownloadUtils.generateId(rankMore.getUrl(), path);

                    DownManger.updateViewHolder(id,new BaseDownViewHolder(id,helper.getView(R.id.k_four),
                            (TextView) helper.getView(R.id.item_down_tv)));

                    int statue = DownManger.isDownStatus(id,path);


                    if(statue == FileDownloadStatus.completed){
                        helper.setGone(R.id.k_four,true);
                        helper.getView(R.id.k_four).setActivated(true);
                        helper.setText(R.id.item_down_tv,"已下载");

                    }else if(statue == FileDownloadStatus.progress){
                        helper.getView(R.id.k_four).setActivated(false);
                        helper.setGone(R.id.k_four,true);
                        helper.setText(R.id.item_down_tv,"下载中...");

                    }else {
                        helper.setGone(R.id.k_four,false);
                    }

                    helper.setGone(R.id.item_tv_num,true);
                    helper.setGone(R.id.item_tv_play_image,false);
                    //哪一个在播放
                    if(isPlay){
                        if(comAll != null){
                            if(rankMore.getId().equals(comAll.getId())){
                                helper.setGone(R.id.item_tv_play_image,true);
                                helper.setGone(R.id.item_tv_num,false);
                            }
                        }
                    }

                    if(isSelMore){

                        helper.setGone(R.id.sel_image,true);

                        if(issetSelList(rankMore)){
                            helper.itemView.setSelected(true);
                        }else {
                            helper.itemView.setSelected(false);
                        }

                    }else {
                        helper.setGone(R.id.sel_image,false);
                    }

                    helper.setText(R.id.item_tv_num,helper.getLayoutPosition() + "");

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

        quickAdapter.addHeaderView(headView);

        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Object item = quickAdapter.getItem(position);
                if(item instanceof  ComAll) {
                    ComAll rankMore = (ComAll) item;
                    if(isSelMore){
                        view.setSelected(!view.isSelected());
                        setSellList(rankMore);
                    }else {
                        setRecord();
                        CommonUtils.getIntent(mFontext,comAllList,position,null);

                    }
                }

            }
        });
        rvList.setAdapter(quickAdapter);

        upPlay();//初始化

    }
    @Override
    public void loadData() {

        if(!CheckUtil.isEmpty(album)){

            GlideUtil.setGlideImageMake(mFontext,album.getCover(),
                    itemImage);
            itemTv.setText(album.getBrief());


            if(GlobalVariable.ONE.equals(album.getIsfav())){
                layout_fav.setSelected(true);
            }else if(GlobalVariable.ZERO.equals(album.getIsfav())){
                layout_fav.setSelected(false);
            }

            if(!CheckUtil.isEmpty(comAllList)){
               quickAdapter.replaceData(comAllList);
               getIdDown();
            }

            if(GlobalVariable.ONE.equals(album.getIspay())){

                xq_gs_layout.setVisibility(View.VISIBLE);

                setLayout(1);

            }else {
                xq_gs_layout.setVisibility(View.GONE);
            }
        }else {


        }

    }
    //检索 id 转换
    private void getIdDown(){

//        DownManger.setIdDown(comAllList, new DownManger.ResultCallback() {
//            @Override
//            public void onSuccess(Object o) {
//                if(!CheckUtil.isEmpty(comAllList)){
//                    IdDown = true;
//                    quickAdapter.replaceData(comAllList);
//                }
//            }
//
//            @Override
//            public void onError(String errString) {
//
//            }
//        });
    }
    private boolean isPay(){
        if(null != album){
            if(GlobalVariable.ONE.equals(album.getIspay())){
                NToast.shortToastBaseApp("付费专辑不能操作");
                return true;
            }else {
                return false;
            }

        }else {
            return true;
        }

    }
    private void setLayout(int layout){
        if(layout == 0){
            xq_layout.setSelected(true);
            gs_layout.setSelected(false);

            quickAdapter.replaceData(new ArrayList<>());

        }else {
            xq_layout.setSelected(false);
            gs_layout.setSelected(true);
            if(!CheckUtil.isEmpty(comAllList))
            quickAdapter.replaceData(comAllList);
        }
    }

    public void postNotifyDataChanged() {
        if (quickAdapter != null) {
            mFontext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (quickAdapter != null) {
                        quickAdapter.replaceData(comAllList);
                    }
                }
            });
        }
    }

    //将activity  中的数据  直接传递过来
    public void setK(BaseResponse<Album, List<ComAll>> k) {
        this.album = k.getInfo();
        this.comAllList = k.getList();
    }

    //排序
    private void setpaixv(){
        if(CheckUtil.isEmpty(comAllList))
            return;


        Collections.reverse(comAllList);// 倒序排列


        quickAdapter.replaceData(comAllList);
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
                sel_all_Layout.setSelected(false);
                if(CheckUtil.isEmpty(selList)){
                    buttomLayout.setVisibility(View.GONE);
                }
                return ;
            }
        }

        selList.add(comAll);

        if(selList.size() == comAllList.size()){
            sel_all_Layout.setSelected(true);
        }

        if(buttomLayout.getVisibility() == View.GONE){
            buttomLayout.setVisibility(View.VISIBLE);
        }

    }

    //在进入播放页面时保存相关信息

    private void setRecord(){

        if(null != album){
            UseData.setWhat(
                    GlobalVariable.TWO,
                    album.getTitle(),
                    id,
                    null,
                    null,
                    -1,
                    -1,
                    null,
                    -1
            );

        }

    }

    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynEvent(Event.PlayEvent event) {
         upPlay();
    }
    public void upPlay(){
        isPlay = isPlay();
        if(isPlay){
            comAll = MusicPlayerManger.getCommAll();
        }
        if(null != quickAdapter)
        quickAdapter.notifyDataSetChanged();

    }
    private boolean isPlay(){
        String iddd = UseData.getUseData().getZBId();

      //  NLog.e(NLog.TAGOther,"iddd--->"+iddd);

        if(!CheckUtil.isEmpty(iddd)){
            if(iddd.equals(id)){
                return true;
            }
        }
        return false;
    }

//    private View getFootView(){

//        if(null == footView){
//            footView = LayoutInflater.from(mFontext).inflate(R.layout.foot_make,null,false);
//        }
//
//        return footView;
 //   }

    //收藏的专辑 取消收藏
    private void user_favorite(final String act){

        UserInfo userInfo = new UserInfo();

        userInfo.setAct(act);
        userInfo.setUsername(UserInfoManger.getInstance().getUserName());
        userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());
        userInfo.setAid(album.getId());

        layout_fav.setEnabled(false);

        baseService.user_favorite(userInfo, new HttpCallback<Object, BaseDataResponse<Object>>() {
                    @Override
                    public void onError(HttpException e) {
                        NToast.shortToastBaseApp(e.getMsg());
                        layout_fav.setEnabled(true);

                    }

                    @Override
                    public void onSuccessAll(BaseDataResponse<Object> k) {
                        NToast.shortToastBaseApp(k.getMsg());

                        layout_fav.setEnabled(true);

                        if(("del").equals(act)){
                            layout_fav.setSelected(false);
                            EventBus.getDefault().post(new Event.FavEvent(2,album.getId()));
                        }else if("add".equals(act)){
                            layout_fav.setSelected(true);
                            EventBus.getDefault().post(new Event.FavEvent(1,album.getId()));

                        }
                    }
                }.setContext(mFontext),
                GroupVoiseFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }


    @Override
    public void onDestroy() {
        quickAdapter = null;
        super.onDestroy();

        EventBus.getDefault().unregister(this);//解除订阅

    }

//       ThreadPoolProxyFactory.getNormalThreadPoolProxy().execute(new Runnable() {
//        @Override
//        public void run() {
//
//            List<ComAll> comAlls = new ArrayList<>();
//
//
//            if(!CheckUtil.isEmpty(comAllList)){
//
//                comAlls.addAll(comAllList);
//
//                for(ComAll comAll : comAlls){
//                    if(DownManger.isDownloaded(comAll.getUrl())){
//                        comAll.setIsDown(1);
//                    }else if(DownManger.isDownStatus(comAll.getUrl() )==  FileDownloadStatus.progress){
//                        comAll.setIsDown(2);
//                    }
//                }
//
//                comAllList.clear();
//                comAllList.addAll(comAlls);
//
//                postNotifyDataChanged();
//
//            }
//        }
//    });

    //    private void setData(){
//
//        private List<List<?>> listList;
//
//        //                isUpOrDown = false;
////                pageNo = 0;
////                listList = splitList(comAllList,15);
////                setData();
//
//        if(pageNo<listList.size()) {
//            if(isUpOrDown){
//                quickAdapter.addData(listList.get(pageNo));
//            }else {
//                quickAdapter.replaceData(listList.get(pageNo));
//            }
//            pageNo ++;
//        }
//    }
//    private   List<List<?>> splitList(List<?> list, int len) {
//        if (list == null || list.size() == 0 || len < 1) {
//            return null;
//        }
//
//        List<List<?>> result = new ArrayList<List<?>>();
//
//
//        int size = list.size();
//        int count = (size + len - 1) / len;
//
//
//        for (int i = 0; i < count; i++) {
//            List<?> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
//            result.add(subList);
//        }
//        return result;
//    }
//    private void  album(){
//        baseService.album(id, 1 + "", "", "", new HttpCallback<Album, BaseResponse<Album, List<ComAll>>>() {
//            @Override
//            public void onError(HttpException e) {
//                if(isUpOrDown){
//                    getRefreshLayout().finishLoadMore();
//                }else {
//                    getRefreshLayout().finishRefresh();
//                }
//                NToast.shortToastBaseApp(e.getMsg());
//            }
//
//            @Override
//            public void onSuccessAll(BaseResponse<Album, List<ComAll>> k) {
//                Album album = k.getInfo();
//                GlideUtil.setGlideImageMake(mFontext,album.getCover(),
//                        itemImage);
//                itemTv.setText(album.getBrief());
//
//                if(!CheckUtil.isEmpty(k.getList())){
//                    quickAdapter.replaceData(k.getList());
//                }
//
//
//            }
//        },this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
//    }


//            rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            //  super.onScrollStateChanged(recyclerView, newState);
//            //当前RecyclerView显示出来的最后一个的item的position
//            int lastPosition = -1;
//
//            //当前状态为停止滑动状态SCROLL_STATE_IDLE时
//            if(newState == RecyclerView.SCROLL_STATE_IDLE) {
//                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                if (layoutManager instanceof LinearLayoutManager) {
//                    lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
//                }
//
//                //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
//                //如果相等则说明已经滑动到最后了
//                if (lastPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
//                    NLog.e(NLog.TAGOther,"到底了---》");
//                }
//            }
//
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            //super.onScrolled(recyclerView, dx, dy);
//
//        }
//    });

}
