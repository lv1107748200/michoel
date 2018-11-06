package fm.qian.michael.ui.activity.dim;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hr.bclibrary.utils.CheckUtil;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.zzhoujay.richtext.RichText;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.common.BaseDownViewHolder;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.db.UseData;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.service.MqService;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.activity.MainActivity;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.Formatter;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.utils.SPUtils;
import fm.qian.michael.widget.dialog.LoadingDialog;
import fm.qian.michael.widget.pop.CustomPopuWindConfig;
import fm.qian.michael.widget.pop.PopPlayListWindow;
import fm.qian.michael.widget.pop.PopShareWindow;
import fm.qian.michael.widget.pop.PopTimingSelWindow;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.widget.single.DownManger;
import fm.qian.michael.widget.single.UserInfoManger;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static fm.qian.michael.common.UserInforConfig.USERMUSICNAME;
import static fm.qian.michael.common.UserInforConfig.USERTMEING;
import static fm.qian.michael.service.MqService.CMDNAME;
import static fm.qian.michael.service.MqService.CMDNOTIF;
import static fm.qian.michael.service.MqService.ERROR_UP;
import static fm.qian.michael.utils.NetStateUtils.isWifi;

/*
 * lv   2018/9/8  音乐播放
 *  opType  状态
 *  0 首次安装第一打开
 *  1 应用第一次打开
 *  2 专辑
 *  3 临时组建的 多选
 *  4 单个音频 //  例如排行榜
 *  5 我的播单
 *  6 只有  id  无播放地址的
 */
public class PlayActivity extends BaseIntensifyActivity implements PopTimingSelWindow.PopTimingSelCallBack {

    private PlaybackStatus mPlaybackStatus;

    public static  String PLAYTUASE = "playTuase";

    private String playTuase;//播放状态 根据不同的状态  进行不同的操作  1 强行更换列表 2继续之前播放

    private String id;

    public  String opType;//操作类型

    private PopTimingSelWindow popTimingSelWindow;
    private PopShareWindow popShareWindow;
    private PopPlayListWindow popPlayListWindow;
    private List<ComAll> comAllList;
    private ComAll comAll;

    private boolean isFristSet = true;//设置时间 进度最大
    private boolean isDown;

    private Disposable mDisposable;//脉搏

    private LoadingDialog loadingDialog;

    @BindView(R.id.image_poster)
    ImageView imagePoster;
    @BindView(R.id.go_home_img)
    ImageView goHomeImg;//返回主页
    @BindView(R.id.set_time_img)
    ImageView setTimeImg;//定时

    @BindView(R.id.layout_play_type)
    ImageView layoutPlayType;//播放方式

    @BindView(R.id.down_image_checked)
    ImageView down_image_checked;//播放方式
    @BindView(R.id.down_image)
    ImageView down_image;//播放方式
    @BindView(R.id.musicSeekBar)
    SeekBar musicSeekBar;
    @BindView(R.id.time_lift_tv)
    TextView timeLiftTv;
    @BindView(R.id.time_right_tv)
    TextView timeRightTv;
    @BindView(R.id.item_ming_tv)
    TextView itemMingTv;//标题
    @BindView(R.id.item_name_tv)
    TextView itemNameTv;//演奏者
    @BindView(R.id.item_time_tv)
    TextView itemTimeTv;//时常
    @BindView(R.id.item_peo_tv)
    TextView itemPeoTv;//播放次数
    @BindView(R.id.item_down_tv)
    TextView itemDownTv;//下载
    @BindView(R.id.introduce_tv)
    TextView introduceTv;//简介

    @BindView(R.id.layout_play)
    LinearLayout layout_play;
    @BindView(R.id.k_one)
    LinearLayout kOne;
    @BindView(R.id.k_two)
    LinearLayout kTwo;
    @BindView(R.id.k_three)
    LinearLayout kThree;
    @BindView(R.id.k_four)
    LinearLayout kFour;

    @OnClick({R.id.base_left_layout,R.id.go_home_img,R.id.set_time_img,
            R.id.layout_play_type,R.id.add_play_list,
            R.id.down_image,R.id.image_share,R.id.layout_play_list
            ,R.id.layout_play,R.id.layout_down,R.id.layout_up
    })
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.base_left_layout:
                finish();
                break;
            case R.id.go_home_img://返回主页
                EventBus.getDefault().post(new Event.MainActivityEvent(0));
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.set_time_img://定时

                if(popTimingSelWindow == null){
                    popTimingSelWindow = new PopTimingSelWindow(this,new CustomPopuWindConfig
                            .Builder(this)
                            .setOutSideTouchable(true)
                            .setFocusable(true)
                            .setAnimation(R.style.popup_hint_anim)
                            .setWith((DisplayUtils.getScreenWidth(this)))
                            .setHigh((DisplayUtils.getScreenHeight(this)/2))
                            .build());
                    popTimingSelWindow.setPopTimingSelCallBack(this);
                    popTimingSelWindow.setQuickAdapter(null,0);
                    popTimingSelWindow.show(setTimeImg);
                    popTimingSelWindow.setSel(UseData.getUseData().getTimeing());

                }else {
                    popTimingSelWindow.setQuickAdapter(null,0);
                    popTimingSelWindow.show(setTimeImg);
                    popTimingSelWindow.setSel(UseData.getUseData().getTimeing());
                }

                break;
            case R.id.layout_up://上一首
                if(isPay()){
                    if(!isLogin()){
                        return;
                    }
                }
                MusicPlayerManger.up();

                break;
            case R.id.layout_down://下一首
                if(isPay()){
                    if(!isLogin()){
                        return;
                    }
                }
                MusicPlayerManger.next();

                break;
            case R.id.layout_play_type://播放方式

                setWhatPattern();//设置播放模式

                break;
            case R.id.layout_play: //播放或暂停
                if(isPay()){
                    if(!isLogin()){
                        return;
                    }
                }
                MusicPlayerManger.pOrq();
                break;
            case R.id.layout_play_list://播放列表

                if(CheckUtil.isEmpty(comAllList)){

                    comAllList = MusicPlayerManger.getCommAllList();

                }

                if(CheckUtil.isEmpty(comAllList)){

                    return;
                }
                if(CheckUtil.isEmpty(comAll)){

                    return;
                }

                if(popTimingSelWindow == null){
                    popTimingSelWindow = new PopTimingSelWindow(this,new CustomPopuWindConfig
                            .Builder(this)
                            .setOutSideTouchable(true)
                            .setFocusable(true)
                            .setAnimation(R.style.popup_hint_anim)
                            .setWith((DisplayUtils.getScreenWidth(this)))
                            .setHigh((DisplayUtils.getScreenHeight(this)/2))
                            .build());
                    popTimingSelWindow.setPopTimingSelCallBack(this);
                    popTimingSelWindow.setQuickAdapter(comAllList,1);
                    popTimingSelWindow.show(setTimeImg);
                    popTimingSelWindow.setComAll(comAll.getId());

                }else {
                    popTimingSelWindow.setQuickAdapter(comAllList,1);
                    popTimingSelWindow.show(setTimeImg);
                    popTimingSelWindow.setComAll(comAll.getId());
                }
                break;
            case R.id.down_image://下载按钮
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }

                if(null != comAll){


                    if(isPay()){
                        NToast.shortToastBaseApp(getString(R.string.付费));
                        return;
                    }
                    if(DownManger.isDownloaded(comAll.getUrl())){
                        NToast.shortToastBaseApp(getString(R.string.已下载));
                        return;
                    }
                        isDown = true;
                    if(isWifi(this)){
                        addPlayerList();//在wifi
                    }else {
                        setDelAlertDialog();//非wifi
                    }

                }

                break;
            case R.id.image_share://分享
//                if(!isLogin()){
//                    WLoaignMake();
//                    return;
//                }
                if(isPay()){
                    NToast.shortToastBaseApp(getString(R.string.付费));
                    return;
                }
                if(null == popShareWindow){
                    popShareWindow = new PopShareWindow(this,new CustomPopuWindConfig.Builder(this)
                            .setOutSideTouchable(true)
                            .setFocusable(true)
                            .setTouMing(false)
                            .setAnimation(R.style.popup_hint_anim)
                            .setWith((com.hr.bclibrary.utils.DisplayUtils.getScreenWidth(this)))
                            .build());
                    popShareWindow.setShareData(new PopShareWindow.ShareData(comAll.getTitle(),comAll.getCover(),comAll.getBrief(),comAll.getShareurl()));

                    popShareWindow.show(view);
                }else {
                    popShareWindow.setShareData(new PopShareWindow.ShareData(comAll.getTitle(),comAll.getCover(),comAll.getBrief(),comAll.getShareurl()));

                    popShareWindow.show(view);
                }

                break;
            case R.id.add_play_list://加入播单
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                if(isPay()){
                    NToast.shortToastBaseApp(getString(R.string.付费));
                    return;
                }
                if(GlobalVariable.FIVE.equals(opType)){

                }
                isDown = false;
                addPlayerList();//仅仅加入波胆
                break;

        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_play;
    }
    @Override
    public void initView() {
        super.initView();
        loadingDialog = new LoadingDialog(this);
        setTitleTv("专辑");

        int h = DisplayUtils.getScreenWidth(this);
        ViewGroup.LayoutParams params = imagePoster.getLayoutParams();
        params.height = h;
       // CommonUtils.setMargins(musicSeekBar,0,h-DisplayUtils.getDimen(R.dimen.margin_6),0,0);


        Intent intent = getIntent();
        playTuase = intent.getStringExtra(PLAYTUASE);

        setInit();
        //setSetTimeImg();
        //setWhatPattern();

        UseData useData = UseData.getUseData();

        setFromUseData(useData);

        opType = useData.getType();



        NLog.e(NLog.PLAYER,"获取状态 --->" + opType);

//        String name = useData.getName();
//        if(!CheckUtil.isEmpty(name)){
//            setTitleTv(name);
//        }
      //  loadingDialog.show("");

        if(GlobalVariable.ZERO.equals(opType)){ //
            comAllList = new ArrayList<>();
            id = UserInfoManger.getInstance().getFirstaudio();
            if(!CheckUtil.isEmpty(id)){
                UseData.setInit(GlobalVariable.ONE);//初始化状态

                List<ComAll> comAlls = new ArrayList<>();
                ComAll comAll = new ComAll();
                comAll.setId(id);
                comAlls.add(comAll);
//                MusicPlayerManger.updata(comAlls,0);
//                MusicPlayerManger.play();

                MusicPlayerManger.synthesizeMake(comAlls,0);

            }

        }else {

            if(MusicPlayerManger.isPlayFirst()){//没有播放
               int i = MusicPlayerManger.play();

               if(i == 1){
                   if(MusicPlayerManger.isPlaying()){
                       layout_play.setSelected(true);
                   }
                   comAll = MusicPlayerManger.getCommAll();
                   if(!CheckUtil.isEmpty(comAll)){
                       isFristSet = true;
                       doSomething();
                       id = comAll.getId();
                       // autio();
                       setMessage();
                   }
               }else if(i == 0){
                   comAll = MusicPlayerManger.getCommAll();
                   if(!CheckUtil.isEmpty(comAll)){
                       isFristSet = true;
                       doSomething();
                       id = comAll.getId();
                       // autio();
                       setMessage();
                   }
               }

            }else {

                //comAllList = MusicPlayerManger.getCommAllList();
                if(MusicPlayerManger.isPlaying()){
                    layout_play.setSelected(true);
                }
                comAll = MusicPlayerManger.getCommAll();
                if(!CheckUtil.isEmpty(comAll)){
                    isFristSet = true;
                    doSomething();
                    id = comAll.getId();
                   // autio();
                    setMessage();
                }else {
                    if(MusicPlayerManger.isNull()){
                        MusicPlayerManger.synthesizeMake(null,0);
                    }
                }

            }

        }

    }


    /**
     * @param num  返回时间编号
     */
    @Override
    public void callBackNum(int num) {
        NLog.e(NLog.PLAYER,"定时编码--->" + num);
        UseData.setSTimeing(num);
        switch (num){
            case 0:
                MusicPlayerManger.timing(-1);//不开启
                break;
            case 1:
                MusicPlayerManger.timing(-2);//播完当前
                break;
            case 2:
                MusicPlayerManger.timing(10*60*1000);//10播完当前
                break;
            case 3:
                MusicPlayerManger.timing(20*60*1000);
                break;
            case 4:
                MusicPlayerManger.timing(30*60*1000);
                break;
            case 5:
                MusicPlayerManger.timing(40*60*1000);
                break;
            case 6:
                MusicPlayerManger.timing(60*60*1000);
                break;
            case 7:
                MusicPlayerManger.timing(90*60*1000);
                break;
            default:

                break;
        }
        setSetTimeImg();
    }

    @Override
    public void callBackComAll(ComAll comAll,int num) {
        MusicPlayerManger.playNum(num);
    }

    private void autio(){
        baseService.audio(id, "", "", new HttpCallback<ComAll, BaseDataResponse<ComAll>>() {
            @Override
            public void onError(HttpException e) {

                NToast.shortToastBaseApp(e.getMsg());

            }

            @Override
            public void onSuccess(ComAll comAll) {

                PlayActivity.this.comAll = comAll;

                setMessage();
            }
        },PlayActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    //定时状态
    private void setSetTimeImg(){
        if( UseData.getUseData().getTimeing() == 0){
            setTimeImg.setSelected(false);
        }else {
            setTimeImg.setSelected(true);
        }
    }
    //设置播放方式
    private void setWhatPattern(){

        int pattern = UseData.getUseData().getPattern();
        int set = 0;

        if( pattern == 0){//循环播放
            set = 1;
        }else if(pattern == 1){//随机
            set = 2;
        }else if(pattern == 2){//单曲
            set = 0;
        }

        if( set == 0){//循环播放
            NToast.shortToastBaseApp("列表循环");
            layoutPlayType.setBackgroundResource(R.drawable.xunhuan);
        }else if(set == 1){//单曲
            NToast.shortToastBaseApp("单曲循环");
            layoutPlayType.setBackgroundResource(R.drawable.xunhuanone);
        }else if(set == 2){//随机
            NToast.shortToastBaseApp("随机播放");
            layoutPlayType.setBackgroundResource(R.drawable.random);
        }
        MusicPlayerManger.playPattern(set);//设置播放模式
        UseData.setWhat(
                null,
                null,
                null,
                null,
                null,
                -1,
                set,
                null,
                -1
        );

    }

    private void setFromUseData(UseData useData){
        if( useData.getTimeing() == 0){
            setTimeImg.setSelected(false);
        }else {
            setTimeImg.setSelected(true);
        }

        int pattern = useData.getPattern();

        if( pattern == 0){//循环播放
            layoutPlayType.setBackgroundResource(R.drawable.xunhuan);
        }else if(pattern == 1){//单曲
            layoutPlayType.setBackgroundResource(R.drawable.xunhuanone);
        }else if(pattern == 2){//随机
            layoutPlayType.setBackgroundResource(R.drawable.random);
        }
    }

    private void setInit(){
        mPlaybackStatus = new PlaybackStatus(this);
        IntentFilter f = new IntentFilter();
        f.addAction(MqService.MUSIC_LODING);//音乐正在加载
        f.addAction(MqService.SEND_PROGRESS);//开始播放获取进度
        f.addAction(MqService.PAUSE_ACTION_APP);//定时器发的刷新图标

        f.addAction(MqService.UPDATA_ID);//换歌了
        f.addAction(MqService.ERROR_UP);//出错
        f.addAction(MqService.UPDATA_PAUSE);//暂停
        f.addAction(CMDNOTIF);//外控设备 启动 播放  但是  此时 service  已经 un
        registerReceiver(mPlaybackStatus, new IntentFilter(f));


        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                dispose();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(isPay()){
                    if(isLogin()){
                        MusicPlayerManger.seek(seekBar.getProgress());
                    }
                }else {
                    MusicPlayerManger.seek(seekBar.getProgress());
                }

            }
        });
    }

    private boolean isPay(){
        if(null != comAll){
            if(GlobalVariable.ONE.equals(comAll.getIspay())){
              //  NToast.shortToastBaseApp("付费专辑不能操作");
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    private void setMessage(){
        if(!isLogin()){
            if(isPay()){//接口返回
                loadingDialog.diss();
                NToast.shortToastBaseApp("付费专辑需登录播放");
            }
        }

        GlideUtil.setGlideImageMake(PlayActivity.this,comAll.getCover(),
                imagePoster);

        setTitleTv(comAll.getTitle());

        itemMingTv.setText(comAll.getTitle());

        if(!CheckUtil.isEmpty(comAll.getBroad())){
            kOne.setVisibility(View.VISIBLE);
            itemNameTv.setText(comAll.getBroad());
        }else {
            kOne.setVisibility(View.GONE);
        }
        if(!CheckUtil.isEmpty(comAll.getPlaynums())){
            kThree.setVisibility(View.VISIBLE);
            itemPeoTv.setText(comAll.getPlaynums());
        }else {
            kThree.setVisibility(View.GONE);
        }
        itemTimeTv.setText(comAll.getMinute()+":"+comAll.getSecond());
        //introduceTv.setText(comAll.getBrief());

        if(!CheckUtil.isEmpty(comAll.getBrief())){
            RichText.from(comAll.getBrief()).bind(this).into(introduceTv);
        }

      String  path = DownManger.createPath(comAll.getUrl());
       int id = FileDownloadUtils.generateId(comAll.getUrl(), path);
        int statue = DownManger.isDownStatus(id, path);

        if(statue == FileDownloadStatus.completed){
            kFour.setVisibility(View.VISIBLE);
            kFour.setActivated(true);
            itemDownTv.setText(getString(R.string.已下载));

            down_image_checked.setVisibility(View.VISIBLE);
           //down_image.setBackgroundResource(R.drawable.down_press);

        }else if (statue == FileDownloadStatus.progress||statue == FileDownloadStatus.started || statue == FileDownloadStatus.connected ) {

            DownManger.updateViewHolder(id,new BaseDownViewHolder(id,-1,down_image,down_image_checked,kFour,itemDownTv));
            kFour.setVisibility(View.VISIBLE);
            kFour.setActivated(true);
            itemDownTv.setText(getString(R.string.下载中));

            down_image_checked.setVisibility(View.GONE);

        }else {
            kFour.setVisibility(View.GONE);
            kFour.setActivated(false);

            down_image_checked.setVisibility(View.GONE);

        }
    }

    private void down(){

        if(null == comAll)
            return;

            List<ComAll> comAlls = new ArrayList<>();
            comAlls.add(comAll);

            if(!CheckUtil.isEmpty(comAlls)){
                DownManger.setIdAndPath(comAlls, null,new DownManger.ResultCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        NToast.shortToastBaseApp(o.toString());
                        String path = DownManger.createPath(comAll.getUrl());
                        int id = FileDownloadUtils.generateId(comAll.getUrl(), path);

                        DownManger.updateViewHolder(id,new BaseDownViewHolder(id,-1,down_image,down_image_checked,kFour,itemDownTv));

                    }

                    @Override
                    public void onError(String errString) {

                    }
                });
            }

    }

    //非wifi网络下的提醒
    private void setDelAlertDialog(){
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

                    addPlayerList();//下载 非WiFi环境

            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    //添加波胆
    private void addPlayerList(){

            if(null == popPlayListWindow){
                popPlayListWindow = new PopPlayListWindow(this,new CustomPopuWindConfig.Builder(this)
                        .setOutSideTouchable(true)
                        .setFocusable(true)
                        .setAnimation(R.style.popup_hint_anim)
                        .setWith((com.hr.bclibrary.utils.DisplayUtils.getScreenWidth(this) - com.hr.bclibrary.utils.DisplayUtils.dip2px(this,80)))
                        .build());
                popPlayListWindow.setPopPlayListWindowCallBack(new PopPlayListWindow.PopPlayListWindowCallBack() {
                    @Override
                    public List<ComAll> getSelComAll() {
                        return Arrays.asList(comAll);
                    }

                    @Override
                    public void state(int what) {
                        if(isDown){
                            down();
                        }

                    }
                });
            }else {

            }
            popPlayListWindow.user_broadcastall();
            popPlayListWindow.show(goHomeImg);

    }

    private  class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<PlayActivity> mReference;


        public PlaybackStatus(final PlayActivity activity) {
            mReference = new WeakReference<>(activity);
        }


        @Override
        public void onReceive(final Context context, final Intent intent) {
            if(null != mReference)
            mReference.get().handleCommandIntent(intent);

        }
    }

    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = MqService.SERVICECMD.equals(action) ? intent.getStringExtra(CMDNAME) : null;

    //    MLog.E( "来自 PlayActivity 广播 : action = " + action + ", command = " + command);
        if (MqService.UPDATA_PAUSE.equals(action)) {
            layout_play.setSelected(false);
            dispose();

        }else if(MqService.SEND_PROGRESS.equals(action)){
            loadingDialog.diss();
            layout_play.setSelected(true);
            isFristSet = true;
            doSomething();
        }else if(MqService.UPDATA_ID.equals(action)){
            String   duration = intent.getStringExtra("PLAYID");

            id = duration;
            NLog.e(NLog.PLAYER, "来自 PlayActivity id = " + id);
            if(!CheckUtil.isEmpty(id)){

                if(popTimingSelWindow != null){
                    if(popTimingSelWindow.getType() == 1){
                        if(popTimingSelWindow.isShowing()){
                            popTimingSelWindow.setComAll(id);
                        }
                    }
                }

                comAll = MusicPlayerManger.getCommAll();
                if(null != comAll)
                setMessage();

              // autio();//service 返回  id;
            }

        }else if(CMDNOTIF.equals(action)){



        }else if(MqService.MUSIC_LODING.equals(action)){

            loadingDialog.show("");

        }else if(MqService.PAUSE_ACTION_APP.equals(action)){
         //   SPUtils.putInt(USERTMEING,0);
            setSetTimeImg();
        }else if(ERROR_UP.equals(action)){
            loadingDialog.diss();
        }
    }


    //获取去 本地保存的记录
    private void getSave(){

        SQLite.select()
                .from(ComAll.class)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<ComAll>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<ComAll> tResult) {
                        NLog.e(NLog.PLAYER, "来自PlayActivity   SQLite  onListQueryResult 查询成功");
                        if(!CheckUtil.isEmpty(tResult)){
                            NLog.e(NLog.PLAYER, "来自PlayActivity   SQLite  onListQueryResult 查询成功" + tResult.size());


                              //  MusicPlayerManger.updata(comAllList,0);
                                MusicPlayerManger.playHistory();

                        }else {

                        }

                    }
                })
                .error(new Transaction.Error() {
                    @Override
                    public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
                        NLog.e(NLog.PLAYER, "来自PlayActivity   SQLite  error"+error.getMessage());
                    }
                })
                .execute();

    }

    public void doSomething() {
        if(null != mDisposable){
            if(!mDisposable.isDisposed()){
                dispose();
            }
        }
        mDisposable = Flowable.interval(0,500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {

                        int duration = (int)MusicPlayerManger.duration();
                        int position = (int) MusicPlayerManger.position();

                        if(isFristSet){
                            isFristSet = false;

                            musicSeekBar.setMax(duration);
                            timeRightTv.setText(Formatter.formatTime(duration));
                        }
                        musicSeekBar.setProgress(position);
                        timeLiftTv.setText(Formatter.formatTime(position));
                    }
                });

    }
    private void dispose(){
        if (mDisposable != null){
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    protected void onDestroy() {
        RichText.clear(this);
        unregisterReceiver(mPlaybackStatus);
        dispose();
        super.onDestroy();
    }
}
