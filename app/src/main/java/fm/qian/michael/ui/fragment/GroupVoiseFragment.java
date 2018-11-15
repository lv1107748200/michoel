package fm.qian.michael.ui.fragment;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hr.bclibrary.utils.CheckUtil;
import com.hr.bclibrary.utils.DisplayUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.zzhoujay.richtext.RichText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
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
import fm.qian.michael.db.DownTasksModel;
import fm.qian.michael.db.UseData;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.base.BaseResponse;
import fm.qian.michael.net.entry.response.Album;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.UserInfo;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.net.http.HttpUtils;
import fm.qian.michael.service.MqService;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.activity.MainActivity;
import fm.qian.michael.ui.activity.SetActivity;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.ui.activity.dim.SearchActivity;
import fm.qian.michael.ui.adapter.MultipleItemPayOrAdapter;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
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
import static fm.qian.michael.utils.CommonUtils.getNumberIndex;
import static fm.qian.michael.utils.DisplayUtils.imageHight1;
import static fm.qian.michael.utils.NetStateUtils.isWifi;

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
  //  private QuickAdapter quickAdapter;//list
    private MultipleItemPayOrAdapter quickAdapter;
    private Album  album;
    private DownTasksModel downTasksModel;

    private List<ComAll> comAllList;
    private List<ComAll> selList;

    private View headView;


    private boolean isSelMore = false;//是否多选操作

    private boolean isPlay = false;


    private ComAll comAll;

    private int showType;

    private boolean isDown;

    TextView itemTv;
    SelectableRoundedImageView itemImage;

    LinearLayout layout_fav,layout_down,
            layout_share,layout_select,
            layout_orderdesc,sel_all_Layout,
            cancel_layout,xq_layout,gs_layout,xq_gs_layout;
    RelativeLayout relayout_sel_cancel,make_relatlayout;

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
                isDown = false;
                addPlayerList();//底部点击

                break;
            case R.id.down_layout://下载
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                if(isPay()){//下载
                    return;
                }

                isDown = true;

                if(isWifi(mFontext)){
                    addPlayerList();//底部点击下载 在 wifi
                }else {
                    setDelAlertDialog(0);
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
//                if(isPay()){//收藏
//                    return;
//                }
                isDown = false;
                if(view.isSelected()){//取消收藏
                    user_favorite("del");//取消收藏
                }else {//收藏
                    user_favorite("add");//点击收藏按钮
                }


                break;
            case R.id.layout_down://下载
                if(!isEableNet()){
                    return;
                }
                if(isSelMore)
                    return;

                if(!isLogin()){
                    WLoaignMake();
                    return;
                }

                if(isPay()){//下载
                    return;
                }

                if(isWifi(mFontext)){
                    if(!layout_fav.isSelected()) {//未收藏
                        isDown = true;
                        user_favorite("add");//wifi环境下载
                    }else {
                        down(comAllList);
                    }
                }else {
                    setDelAlertDialog(1);
                }

                break;
            case R.id.layout_share://分享
//                if(!isLogin()){
//                    WLoaignMake();
//                    return;
//                }
                if(!isEableNet()){
                    return;
                }
                if(isPay()){//分享
                    return;
                }

                if(GlobalVariable.ONE.equals(album.getIspay())){ // 0不是  1是
                    NToast.shortToastBaseApp(getString(R.string.付费));
                    return;
                }

                if(null == popShareWindow){
                    popShareWindow = new PopShareWindow(mFontext,new CustomPopuWindConfig.Builder(mFontext)
                            .setOutSideTouchable(true)
                            .setFocusable(true)
                            .setTouMing(false)
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
                sel_all_Layout.setSelected(false);
                quickAdapter.notifyDataSetChanged();

                break;
            case R.id.xq_layout:
                setLayout(0);//详情
                break;
            case R.id.gs_layout:
                setLayout(1);//故事
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
        make_relatlayout = headView.findViewById(R.id.make_relatlayout);


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

        LayoutParmsUtils.setHight(imageHight1(3,0,0),itemImage);


        rvList.setLayoutManager(new LinearLayoutManager(mFontext));

        ((SimpleItemAnimator)rvList.getItemAnimator()).setSupportsChangeAnimations(false);

        quickAdapter = new MultipleItemPayOrAdapter(null){

            @Override
            protected void convert(BaseViewHolder helper, MultiItemEntity item) {
                switch (helper.getItemViewType()) {
                    case WEB:
                        if(item instanceof Album){
                            setWebView((WebView) helper.getView(R.id.webview),((Album) item).getBrief_pay());
                        }
                        break;
                    case LIST:
                        if(item instanceof  ComAll) {
                            ComAll rankMore = (ComAll) item;

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

                            int statue = DownManger.isDownStatus(id, path);


                            if (statue == FileDownloadStatus.completed) {
                                helper.setGone(R.id.k_four, true);
                                helper.getView(R.id.k_four).setActivated(true);
                                helper.setText(R.id.item_down_tv,getString(R.string.已下载));

                            } else if (statue == FileDownloadStatus.progress||statue == FileDownloadStatus.started || statue == FileDownloadStatus.connected ) {
                                helper.getView(R.id.k_four).setActivated(false);
                                helper.setGone(R.id.k_four, true);
                                helper.setText(R.id.item_down_tv,getString(R.string.下载中));

                            } else {
                                helper.setGone(R.id.k_four, false);
                            }

                            helper.setGone(R.id.item_tv_num, true);
                            helper.setGone(R.id.item_tv_play_image, false);
                            //哪一个在播放
                            if (isPlay) {
                                if (comAll != null) {
                                    if (rankMore.getId().equals(comAll.getId())) {
                                        helper.setGone(R.id.item_tv_play_image, true);
                                        helper.setGone(R.id.item_tv_num, false);
                                    }
                                }
                            }

                            if (isSelMore) {

                                helper.setGone(R.id.sel_image, true);

                                if (issetSelList(rankMore)) {
                                    helper.itemView.setSelected(true);
                                } else {
                                    helper.itemView.setSelected(false);
                                }

                            } else {
                                helper.setGone(R.id.sel_image, false);
                            }

                            helper.setText(R.id.item_tv_num, getNumberIndex(helper.getLayoutPosition()));

                            helper.setText(R.id.item_ming_tv, rankMore.getTitle());

                            if (CheckUtil.isEmpty(rankMore.getBroad())) {
                                helper.setGone(R.id.k_one, false);
                            } else {
                                helper.setGone(R.id.k_one, true);
                                helper.setText(R.id.item_name_tv, rankMore.getBroad());
                            }
                            if (CheckUtil.isEmpty(rankMore.getPlaynums())) {
                                helper.setGone(R.id.k_two, false);
                            } else {
                                helper.setGone(R.id.k_two, true);
                                helper.setText(R.id.item_peo_tv, rankMore.getPlaynums());
                            }
                            helper.setText(R.id.item_time_tv, rankMore.getMinute() + ":" + rankMore.getSecond());

                            DownManger.setImageView((ImageView) helper.getView(R.id.head_portrait),
                                    rankMore.getCover_small(),mFontext);
                        }
                        break;
                }
            }
//            @Override
//            public void onViewRecycled(@NonNull BaseViewHolder holder) {
//                super.onViewRecycled(holder);
//                // NLog.e(NLog.TAGDOWN," 视图 onViewRecycled : " + holder.getLayoutPosition()+1);
//
//                if(null != holder){
//                    Object item = quickAdapter.getItem(holder.getLayoutPosition()+1);
//                    if(item instanceof ComAll){
//                        ComAll rankMore = (ComAll) item;
//                        String path ;
//                        int id ;
//                        //  NLog.e(NLog.TAGDOWN," 视图 下载id : " + id);
//
//                        if(CheckUtil.isEmpty(rankMore.getDownPath())){
//                            path = DownManger.createPath(rankMore.getUrl());
//                            id = FileDownloadUtils.generateId(rankMore.getUrl(), path);
//                        }else {
//                            id =  rankMore.getIsDown();
//                        }
//
//                        DownManger.updateViewHolder(id);
//                    }
//                }
//
//            }
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
        DownManger.setQAdapter(quickAdapter);

        rvList.setAdapter(quickAdapter);

        upPlay();//初始化

    }
    @Override
    public void loadData() {

        if(!CheckUtil.isEmpty(album)){

            GlideUtil.setGlideImageMake(mFontext,album.getCover(),
                    itemImage);

            if(!CheckUtil.isEmpty(album.getBrief())){
                RichText.from(album.getBrief()).bind(this).into(itemTv);
            }
//            itemTv.setText(album.getBrief());


            if(GlobalVariable.ONE.equals(album.getIsfav())){
                layout_fav.setSelected(true);
            }else if(GlobalVariable.ZERO.equals(album.getIsfav())){
                layout_fav.setSelected(false);
            }

            if(!CheckUtil.isEmpty(comAllList)){
               quickAdapter.replaceData(comAllList);
            }

            if(GlobalVariable.ONE.equals(album.getIspay())){

                xq_gs_layout.setVisibility(View.VISIBLE);

                setLayout(1);

            }else {
                xq_gs_layout.setVisibility(View.GONE);
            }
        }else if(!CheckUtil.isEmpty(downTasksModel)) {
//            String pathImage = DownManger.createImagePath(downTasksModel.getCover());
//            int idImage = FileDownloadUtils.generateId(downTasksModel.getCover(), pathImage);
//            int statueImage = DownManger.isDownStatus(idImage, pathImage);
//
//            if(statueImage == FileDownloadStatus.completed){
//                GlideUtil.setGlideImageMake(mFontext,pathImage,
//                        itemImage);
//            }else {
//                GlideUtil.setGlideImageMake(mFontext,downTasksModel.getCover(),
//                        itemImage);
//            }
            DownManger.setImageView(itemImage,downTasksModel.getCover(),mFontext);

            if(!CheckUtil.isEmpty(downTasksModel.getBrief())){
                RichText.from(downTasksModel.getBrief()).bind(this).into(itemTv);
            }
            if(!CheckUtil.isEmpty(comAllList)){
                getIdDown();
//                quickAdapter.replaceData(comAllList);
            }
            make_relatlayout.setVisibility(View.GONE);
        }

    }
    //检索 id 转换
    private void getIdDown(){

        if(!CheckUtil.isEmpty(downTasksModel.getAllJson())){
            List<String> stringList = HttpUtils.jsonToBeanT(downTasksModel.getAllJson(),
                    new TypeReference<List<String>>() {});

            NLog.e(NLog.TAGDOWN, "数据处理json " + downTasksModel.getAllJson());

            if(!CheckUtil.isEmpty(stringList)){

                List<ComAll> comAlls = new ArrayList<>();

                for(String s : stringList){
                    for(ComAll comAll : comAllList){
                        if(s.equals(comAll.getId())){
                            comAlls.add(comAll);
                            continue;
                        }
                    }
                }

                comAllList.clear();
                comAllList.addAll(comAlls);

                quickAdapter.replaceData(comAllList);

            }
        }else {
            quickAdapter.replaceData(comAllList);
        }

    }
    public final static String CSS_STYLE ="<style>* {font-size:15px;line-height:20px;}p {color:#666666;}</style>";
    private void setWebView(WebView webview,String content) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setDefaultTextEncodingName("UTF-8") ;
        webview.getSettings().setBlockNetworkImage(false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            webview.getSettings().setMixedContentMode(webview.getSettings()
                    .MIXED_CONTENT_ALWAYS_ALLOW);  //注意安卓5.0以上的权限
        }
        webview.loadDataWithBaseURL(null,CSS_STYLE+CommonUtils.getNewContent(content),
                "text/html", "UTF-8", null);
    }
    private boolean isPay(boolean is){
        if(null != album){
            if(GlobalVariable.ONE.equals(album.getIspay())){
                if(is)
                NToast.shortToastBaseApp(getString(R.string.付费));
                return true;
            }else {
                return false;
            }

        }else {
            NToast.shortToastBaseApp("数据为空不能操作");
            return true;
        }

    }
    private boolean isPay(){
        if(null != album){
            if(GlobalVariable.ONE.equals(album.getIspay())){
                    NToast.shortToastBaseApp(getString(R.string.付费));
                return true;
            }else {
                return false;
            }

        }else {
            NToast.shortToastBaseApp("数据为空不能操作");
            return false;
        }

    }
    private void setLayout(int layout){
        showType = layout;
        if(layout == 0){
            xq_layout.setSelected(true);
            gs_layout.setSelected(false);

            if(!CheckUtil.isEmpty(album))
            quickAdapter.replaceData(Arrays.asList(album));

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
    public void setK(DownTasksModel k) {
        this.downTasksModel = k;
        this.comAllList = k.getComAlls();
    }

    //排序
    private void setpaixv(){
        if(CheckUtil.isEmpty(comAllList))
            return;

        if(isPay(false)){ //排序问题
            if(showType == 1){
                Collections.reverse(comAllList);// 倒序排列
                quickAdapter.replaceData(comAllList);
            }else {
                Collections.reverse(comAllList);// 倒序排列
            }
        }else {
            Collections.reverse(comAllList);// 倒序排列
            quickAdapter.replaceData(comAllList);
        }

        if(layout_orderdesc.isSelected()){
            layout_orderdesc.setSelected(false);
        }else {
            layout_orderdesc.setSelected(true);
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
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynLogGEvent(Event.LoginEvent event) {

        String id = event.getId();

        //登陆后刷新
        if(GlobalVariable.TWO.equals(id)){
            album();
        }

    }
    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynEventtG(Event.PlayEvent event) {

        int i = event.getI();

        if(i == 1){
            upPlay();
        }else if(i == 2){
            if(null != quickAdapter)
                quickAdapter.notifyDataSetChanged();
        }

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
//        String iddd = UseData.getUseData().getZBId();
//
//      //  NLog.e(NLog.TAGOther,"iddd--->"+iddd);
//
//        if(!CheckUtil.isEmpty(iddd)){
//            if(iddd.equals(id)){
//                return true;
//            }
//        }
        return true;
    }

//    private View getFootView(){

//        if(null == footView){
//            footView = LayoutInflater.from(mFontext).inflate(R.layout.foot_make,null,false);
//        }
//
//        return footView;
 //   }
private void  album(){
    baseService.album(id, 1 + "", "", "", new HttpCallback<Album, BaseResponse<Album, List<ComAll>>>() {
        @Override
        public void onError(HttpException e) {
            NToast.shortToastBaseApp(e.getMsg());
        }

        @Override
        public void onSuccessAll(BaseResponse<Album, List<ComAll>> k) {
            setK(k);
            loadData();
        }
    }, GroupVoiseFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
}

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


                        layout_fav.setEnabled(true);

                        if(("del").equals(act)){
                            NToast.shortToastBaseApp("取消收藏");
                            layout_fav.setSelected(false);
                            EventBus.getDefault().post(new Event.FavEvent(2,album.getId()));
                        }else if("add".equals(act)){
                            NToast.shortToastBaseApp("收藏成功");
                            layout_fav.setSelected(true);
                            if(isDown){
                                down(comAllList);
                            }
                            EventBus.getDefault().post(new Event.FavEvent(1,album.getId()));
                        }


                    }
                }.setContext(mFontext),
                GroupVoiseFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }
    //下载管理
    private void down(List<ComAll> list){
        if(!CheckUtil.isEmpty(list)){
            showLoadingDialog("");
            DownManger.setIdAndPath(comAllList,album,list,null,new DownManger.ResultCallback() {
                @Override
                public void onSuccess(Object baseDownloadTaskSparseArray) {
                    if(null != quickAdapter){
                        quickAdapter.notifyDataSetChanged();
                        NToast.shortToastBaseApp(getString(R.string.成功添加下载任务));
                    }
                    dissLoadingDialog();
                }
                @Override
                public void onError(String errString) {

                }
            });
        }else {
            NToast.shortToastBaseApp(getString(R.string.请选择));
        }
    }
    //添加波胆
    private void addPlayerList(){

        if(isDown){
            down(selList);
        }else {
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

    }

    //非wifi网络下的提醒
    private void setDelAlertDialog(final int showType){
        AlertDialog.Builder builder = new AlertDialog.Builder(mFontext);
        builder.setTitle("提示");
        builder.setMessage(getString(R.string.WiFi));
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(showType == 0){//添加波丹后下载
                    addPlayerList();//下载 非WiFi环境
                }else if(showType == 1) {//收藏后下载
                    if(!layout_fav.isSelected()) {//未收藏
                        isDown = true;
                        user_favorite("add");//非WiFi环境
                    }else {
                        down(comAllList);
                    }
                }

            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }


    @Override
    public void onDestroy() {
        quickAdapter = null;
        RichText.clear(this);
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
//    }    private void  album(){
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
