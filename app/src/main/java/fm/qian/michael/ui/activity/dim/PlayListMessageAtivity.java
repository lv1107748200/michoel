package fm.qian.michael.ui.activity.dim;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseRecycleViewActivity;
import fm.qian.michael.common.BaseDownViewHolder;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.db.UseData;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.Video;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.UserInfo;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.custom.SelectableRoundedImageView;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.widget.dialog.LoadingDialog;
import fm.qian.michael.widget.pop.CustomPopuWindConfig;
import fm.qian.michael.widget.pop.PopInputWindow;
import fm.qian.michael.widget.single.DownManger;
import fm.qian.michael.widget.single.UserInfoManger;

import static fm.qian.michael.utils.NetStateUtils.isWifi;

/*
 * lv   2018/9/10
 */
public class PlayListMessageAtivity extends BaseRecycleViewActivity implements View.OnClickListener {

    public static final String PLAYLIST = "PlayList";
    public static final String PLAYLISTNAME = "PlayListName";

    private boolean isSelMore = false;//是否多选操作
    private QuickAdapter quickAdapter;

    private List<ComAll> comAllList;

    private List<ComAll> selList;

    private boolean isPlay = false;
    private ComAll comAll;

    private String bid;

    private LoadingDialog loadingDialog;

    @BindView(R.id.buttom_layout)
    LinearLayout buttomLayout;
    @BindView(R.id.add_layout)
    LinearLayout add_layout;
    @BindView(R.id.del_layout)
    LinearLayout del_layout;
    @BindView(R.id.play_layout)
    LinearLayout play_layout;

    private LinearLayout layout_editname,
            layout_down,layout_mang,
            layout_del, sel_all_Layout,
            cancel_layout;

    private RelativeLayout relayout_sel_cancel;

    private PopInputWindow popInputWindow;

    @OnClick({R.id.base_left_layout,
            R.id.base_right_layout
    ,R.id.down_layout,R.id.del_layout})
    public void On(View view){
        switch (view.getId()) {
            case R.id.base_left_layout:
                finish();
                break;
            case R.id.base_right_layout:

                break;
            case R.id.down_layout://下载
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                if(!CheckUtil.isEmpty(selList)){
                        if(isWifi(this)){
                            down(selList);
                        }else {
                            setDelAlertDialog(1);//选择下载
                        }
                }else {
                    NToast.shortToastBaseApp(getString(R.string.请选择));
                }

                break;
            case R.id.del_layout://删除
                if(isSelMore){
                    if(!CheckUtil.isEmpty(selList)){

                        setUserInfoDelList( CommonUtils.setJoint(selList));

                        deldelListFile(selList);
                    }
                }

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_editname:
                if(!isLogin()){
                    NToast.shortToastBaseApp(getString(R.string.需登陆));
                    return;
                }

                if(null == popInputWindow){
                    popInputWindow = new PopInputWindow(this,new  CustomPopuWindConfig.Builder(this)
                            .setOutSideTouchable(true)
                            .setFocusable(true)
                            .setAnimation(R.style.popup_hint_anim)
                            .setWith((com.hr.bclibrary.utils.DisplayUtils.getScreenWidth(this) - com.hr.bclibrary.utils.DisplayUtils.dip2px(this,80)))
                            .build() );

                    popInputWindow.setPopInputWindowCallBack(new PopInputWindow.PopInputWindowCallBack() {
                        @Override
                        public void callBackInputText(String text) {


                            if(!CheckUtil.isEmpty(text)){
                                user_broadcast("update",text);
                            }else {
                                NToast.shortToastBaseApp("请输入播单名称");
                            }


                        }
                    });
                    popInputWindow.setMain_search("请输入播单名称");
                    popInputWindow.setSet_add_tv_title("更改播单");

                    popInputWindow.show(v);
                }else {
                    popInputWindow.show(v);
                }

                break;
            case R.id.layout_down:

                if(!isLogin()){
                    NToast.shortToastBaseApp(getString(R.string.需登陆));
                    return;
                }

                if(isSelMore)
                    return;

                if(!CheckUtil.isEmpty(comAllList)){
                if(isWifi(this)){
                    down(comAllList);
                }else {
                    setDelAlertDialog(0);//全部下载
                }
                }

                break;
            case R.id.layout_mang://管理故事

                isSelMore = !isSelMore;

                if(isSelMore){
                    relayout_sel_cancel.setVisibility(View.VISIBLE);
                }else {
                    //buttomLayout.setVisibility(View.GONE);
                }

                quickAdapter.notifyDataSetChanged();

                break;
            case R.id.layout_del://删除波胆
                setDelAlertDialog();
                break;
            case R.id.sel_all_Layout://全选

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
            case R.id.cancel_layout://取消

                isSelMore = !isSelMore;

                buttomLayout.setVisibility(View.GONE);
                relayout_sel_cancel.setVisibility(View.GONE);

                if(!CheckUtil.isEmpty(selList)) {
                    selList.clear();
                }
                sel_all_Layout.setSelected(false);
                quickAdapter.notifyDataSetChanged();

                break;
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_play_list_meg;
    }

    @Override
    public void initView() {
        super.initView();

//        if(!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }

        setTitleTv("我的播单");
        add_layout.setVisibility(View.GONE);//隐藏添加按钮
        play_layout.setVisibility(View.GONE);//隐藏播放按钮
        del_layout.setVisibility(View.VISIBLE);//显示删除按钮
    }

    @Override
    public void initData() {
        super.initData();

        loadingDialog = new LoadingDialog(this);

        bid = getIntent().getStringExtra(PLAYLIST);

        String name = getIntent().getStringExtra(PLAYLISTNAME);

        if(!CheckUtil.isEmpty(name)){
            setTitleTv(name);
        }

        View view = LayoutInflater.from(this).inflate(R.layout.item_play_list_make,null,false);

        layout_editname = view.findViewById(R.id.layout_editname);
        layout_down = view.findViewById(R.id.layout_down);
        layout_mang = view.findViewById(R.id.layout_mang);
        layout_del = view.findViewById(R.id.layout_del);
        sel_all_Layout = view.findViewById(R.id.sel_all_Layout);
        cancel_layout = view.findViewById(R.id.cancel_layout);
        relayout_sel_cancel = view.findViewById(R.id.relayout_sel_cancel);

        layout_editname.setOnClickListener(this);
        layout_down.setOnClickListener(this);
        layout_mang.setOnClickListener(this);
        layout_del.setOnClickListener(this);
        sel_all_Layout.setOnClickListener(this);
        cancel_layout.setOnClickListener(this);
        relayout_sel_cancel.setOnClickListener(this);

        getRvList().setLayoutManager(new LinearLayoutManager(this));

        ((SimpleItemAnimator)getRvList().getItemAnimator()).setSupportsChangeAnimations(false);

        quickAdapter =  new QuickAdapter(R.layout.item_sel_voice){
            @Override
            protected void convert(BaseViewHolder helper, Object item) {
                if(item instanceof ComAll){
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

                       // NLog.e(NLog.TAGDOWN," 视图 下载id : " + id);

                         DownManger.updateViewHolder(id,new BaseDownViewHolder(id,helper.getLayoutPosition(),helper.getView(R.id.k_four),
                            (TextView) helper.getView(R.id.item_down_tv)));

                        int statue = DownManger.isDownStatus(id,path);

                        if(statue == FileDownloadStatus.completed){
                            helper.setGone(R.id.k_four,true);
                            helper.getView(R.id.k_four).setActivated(true);
                            helper.setText(R.id.item_down_tv,getString(R.string.已下载));

                        }else if(statue == FileDownloadStatus.progress||statue == FileDownloadStatus.started || statue == FileDownloadStatus.connected ){
                            helper.getView(R.id.k_four).setActivated(false);
                            helper.setGone(R.id.k_four,true);
                            helper.setText(R.id.item_down_tv,getString(R.string.下载中));

                        }else {

                            helper.setGone(R.id.k_four,false);

                        }

                    //哪一个在播放
                    helper.setGone(R.id.item_tv_play_image,false);
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

                    GlideUtil.setGlideImageMake(PlayListMessageAtivity.this,rankMore.getCover_small(),
                            (ImageView) helper.getView(R.id.head_portrait));
                }

            }


//            @Override
//            public void onViewRecycled(@NonNull BaseViewHolder holder) {
//                super.onViewRecycled(holder);
//               // NLog.e(NLog.TAGDOWN," 视图 onViewRecycled : " + holder.getLayoutPosition()+1);
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
//
//                    }
//                }
//            }

        };

        quickAdapter.addHeaderView(view);

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
                        CommonUtils.getIntent(PlayListMessageAtivity.this,comAllList,position,null);
                    }
                }
            }
        });

     DownManger.setQAdapter(quickAdapter);

        getRvList().setAdapter(quickAdapter);
        selList = new ArrayList<>();


        if(null != bid){
            loadingDialog.show("正在加载");
            setUserInfo("list",bid);
        }

    //    upPlay();

    }

    @Override
    public void loadMore() {
        super.loadMore();
    }

    @Override
    public void Refresh() {
        super.Refresh();
    }

    @Override
    public boolean isDamp() {
        return true;
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
            if(iddd.equals(bid)){
                return true;
            }
        }
        return false;
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

    private void setUserInfo(String act,String p){

        UserInfo data = new UserInfo();
        data.setAct(act);
        data.setUsername(UserInfoManger.getInstance().getUserName());
        data.setSessionkey(UserInfoManger.getInstance().getSessionkey());
        if(null != p)
            data.setP(p);

        data.setBid(bid);

        user_broadcastlistAll(data);
    }

    private void user_broadcastlistAll(UserInfo data){
        baseService.user_broadcastlistAll(
                data,
                new HttpCallback<List<ComAll>, BaseDataResponse<List<ComAll>>>() {
                    @Override
                    public void onError(HttpException e) {
                        NToast.shortToastBaseApp(e.getMsg());

                        loadingDialog.diss();
                    }

                    @Override
                    public void onSuccess(List<ComAll> comAlls) {
                        loadingDialog.diss();

                        if(!CheckUtil.isEmpty(comAlls)){
                            comAllList = comAlls;
                            quickAdapter.replaceData(comAllList);
                            getIdDown();
                        }else {
                            comAllList = new ArrayList<>();
                            quickAdapter.replaceData(comAllList);
                        }


                    }
                }.setContext(this),PlayListMessageAtivity.this.bindUntilEvent(ActivityEvent.DESTROY)

        );
    }

    private void user_broadcast(final String act,final String title){

        UserInfo data = new UserInfo();

        data.setAct(act);
        data.setUsername(UserInfoManger.getInstance().getUserName());
        data.setSessionkey(UserInfoManger.getInstance().getSessionkey());
        data.setBid(bid);

        if(null != title){
            data.setTitle(title);
        }

        baseService.user_broadcast(data, new HttpCallback<ComAll, BaseDataResponse>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());

                if(null != popInputWindow)
                    popInputWindow.dismiss();

            }

            @Override
            public void onSuccessAll(BaseDataResponse k) {

                if(null != popInputWindow)
                    popInputWindow.dismiss();
                NToast.shortToastBaseApp(k.getMsg());

                if("update".equals(act)){
                    setTitleTv(title);
                }


                EventBus.getDefault().post(new Event.LoginEvent(GlobalVariable.ONE));

                if("del".equals(act)){
                    deldelListFile(comAllList);
                    finish();
                }

            }
        }.setContext(this),PlayListMessageAtivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }


    private void setUserInfoDelList(String mids){

        UserInfo data = new UserInfo();
        data.setAct("del");
        data.setUsername(UserInfoManger.getInstance().getUserName());
        data.setSessionkey(UserInfoManger.getInstance().getSessionkey());
        if(null != mids)
            data.setMids(mids);
        if(null != bid)
            data.setBid(bid);

        user_broadcastlist(data);

    }
    //删除
    private void user_broadcastlist(UserInfo data){
         baseService.user_broadcastlist(data, new HttpCallback<Object, BaseDataResponse<Object>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(Object comAll) {

                EventBus.getDefault().post(new Event.LoginEvent(GlobalVariable.ONE));
                setUserInfo("list",bid);//删除成功后

            }
        }.setContext(this), PlayListMessageAtivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    private void deldelListFile(List<ComAll> comAlls){
        DownManger.delListFile(comAlls, new DownManger.ResultCallback() {
            @Override
            public void onSuccess(Object o) {
                NToast.shortToastBaseApp("删除成功");
            }

            @Override
            public void onError(String errString) {

            }
        });
    }

    //在进入播放页面时保存相关信息
    private void setRecord(){

            UseData.setWhat(
                    GlobalVariable.FIVE,
                    "我的播单",
                    bid,
                    null,
                    null,
                    -1,
                    -1,
                    null,
                    -1
            );

    }

    //删除播单时 警示框

    private void setDelAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确认要删除此播单？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isLogin()){
                    user_broadcast("del",null);

                }

            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    //非wifi网络下的提醒
    private void setDelAlertDialog(final int showType){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    down(comAllList);
                }else if(showType == 1){
                    down(selList);
                }
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
