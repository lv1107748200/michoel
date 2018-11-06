package fm.qian.michael.ui.activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hr.bclibrary.utils.CheckUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.zzhoujay.richtext.RichText;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseExitActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.db.TasksManagerModel;
import fm.qian.michael.db.TasksManagerModel_Table;
import fm.qian.michael.db.UseData;

import fm.qian.michael.service.MqService;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.ui.activity.dim.SearchActivity;
import fm.qian.michael.ui.adapter.MainAdapter;
import fm.qian.michael.ui.fragment.ArticleFragment;
import fm.qian.michael.ui.fragment.CategoryFragment;
import fm.qian.michael.ui.fragment.FindFragment;
import fm.qian.michael.ui.fragment.MyFragment;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.utils.NetStateUtils;
import fm.qian.michael.widget.broadcast.NetworkConnectChangedReceiver;
import fm.qian.michael.widget.custom.BottomBarLayout;
import fm.qian.michael.widget.custom.XCViewPager;
import fm.qian.michael.widget.single.DownManger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.widget.single.UserInfoManger;

import static fm.qian.michael.service.MqService.CMDNOTIF;

public class MainActivity extends BaseExitActivity implements BottomBarLayout.BottomCallBack,
        NetworkConnectChangedReceiver.BroadcastCallBack {

//    private LoadingDialog loadingDialog;
    private PlaybackStatus mPlaybackStatus;


    private MainAdapter mainAdapter;

    @BindView(R.id.play_image)
    ImageView playImage;
    @BindView(R.id.xViewPager_main)
    XCViewPager xViewPagerMain;
    @BindView(R.id.bottom_layout)
    BottomBarLayout bottomLayout;
    @BindView(R.id.search_layout)
    LinearLayout searchLayout;
    @BindView(R.id.layout_lift)
    LinearLayout layout_lift;
    @BindView(R.id.layout_right)
    LinearLayout layout_right;

    @OnClick({R.id.search_layout, R.id.layout_lift,R.id.layout_right})
    public void onClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.search_layout:



                break;
            case R.id.layout_lift:
//                String kind = String.valueOf(xViewPagerMain.getCurrentItem());
                intent.setClass(this,SearchActivity.class);
              //  intent.putExtra("kind",kind);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, searchLayout, "shareNames").toBundle());
                }
                else {
                    startActivity(intent);
                }
//                loadingDialog = new LoadingDialog(this).creatDialog();
//                loadingDialog.show("正在加载...");
                break;
            case R.id.layout_right:

                intent.setClass(this,PlayActivity.class);
                if(!GlobalVariable.ZERO.equals(UseData.getUseData().getType())){
                    startActivity(intent);
                }else {
                    if(!CheckUtil.isEmpty(UserInfoManger.getInstance().getFirstaudio())){
                        startActivity(intent);
                    }
                }

                break;
        }
    }
    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//
//        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
//            //获取ActivityManager
//            ActivityManager mAm = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
//            //获得当前运行的task
//            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
//            for (ActivityManager.RunningTaskInfo rti : taskList) {
//                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
//                if(rti.topActivity.getPackageName().equals(getPackageName())) {
//                    mAm.moveTaskToFront(rti.id,0);
//                    return;
//                }
//            }
//        }else {
//            Intent intent = new Intent(this,SplashActivity.class);
//            startActivity(intent);
//        }
//       // setTheme(R.style.MyTheme);
//        super.onCreate(savedInstanceState);
//    }
    @Override
    public void initTitle() {
        if(!NToast.isNotificationEnabled(this)){
            setDelAlertDialog();
        }
        mPlaybackStatus = new PlaybackStatus(this);
        IntentFilter f = new IntentFilter();
        f.addAction(MqService.SEND_PROGRESS);//开始播放获取进度
        f.addAction(MqService.UPDATA_PAUSE);//暂停
        f.addAction(MqService.UPDATA_ID);//换歌
        f.addAction(CMDNOTIF);//外控设备 启动 播放  但是  此时 service  已经 un
        registerReceiver(mPlaybackStatus, new IntentFilter(f));

        GlideUtil.setGlideImage(this, R.drawable.pause,playImage);
    }

    @Override
    public void initView() {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        xViewPagerMain.setOffscreenPageLimit(4);

        bottomLayout.setBottomCallBack(this);
        bottomLayout.setViewPager(xViewPagerMain);

        //setStatusBar(statusBar);

        setPermission();
        // setBroadcast(this);

        if(MusicPlayerManger.isPlaying()){
            GlideUtil.setGif(this, R.mipmap.playing,playImage);
        }else {
            GlideUtil.setGlideImage(this, R.drawable.pause,playImage);
        }

        setBroadcast(this);
    }

    @Override
    public void initData() {

        List<BottomBarLayout.DataList> dataLists = new ArrayList<>();
        int[] strings = {R.string.发现, R.string.分类, R.string.文章, R.string.我的};
        int[] stringId = {fm.qian.michael.R.drawable.selector_find,
                R.drawable.selector_category,
                R.drawable.selector_article,
                R.drawable.selector_my};

        for(int i =0,j=strings.length;i<j;i++){
            BottomBarLayout.DataList dataList = new BottomBarLayout.DataList(stringId[i],strings[i]);

            dataLists.add(dataList);
        }

        bottomLayout.init(this,dataLists);

        mainAdapter = new MainAdapter(getSupportFragmentManager());
        xViewPagerMain.setAdapter(mainAdapter);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new FindFragment());
        fragmentList.add(new CategoryFragment());
        fragmentList.add(new ArticleFragment());
        fragmentList.add(new MyFragment());


        mainAdapter.upData(fragmentList);

        bottomLayout.setSelect(0);
        bottomLayout.setBomSel(0);

        autoDown();//检测自动下载
    }

    //点击安全企业
    @Override
    public void onSafetyEnterprise() {

        // NLog.e(NLog.TAG,"onSafetyEnterprise");
    }

    @Override
    public void onBottomTab(int num) {

//        if(num == 0){
//            layout_lift.setVisibility(View.VISIBLE);
//            layout_right.setVisibility(View.VISIBLE);
//        }else {
//            layout_lift.setVisibility(View.GONE);
//            layout_right.setVisibility(View.GONE);
//
//        }

    }
    //网络状态广播
    @Override
    public void setState(boolean is) {
        if(is){
            EventBus.getDefault().post(new Event.NetEvent(1));
        }else {
          //  NToast.shortToastBaseApp("无网络连接！");
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(Event.MainActivityEvent event) {

        int sel = event.getSel();

        if(sel == 4){
            mainAdapter.updata();
        }else {
            bottomLayout.setSelect(sel);
        }

    }


    private  class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<MainActivity> mReference;


        public PlaybackStatus(final MainActivity activity) {
            mReference = new WeakReference<>(activity);
        }


        @Override
        public void onReceive(final Context context, final Intent intent) {
             String action = intent.getAction();
            if (MqService.UPDATA_PAUSE.equals(action)) {
                if(null != mReference){
                    GlideUtil.setGlideImage(mReference.get(), R.drawable.pause,playImage);
                }
            }else if(MqService.SEND_PROGRESS.equals(action)){

                if(null != mReference){
                    GlideUtil.setGif(mReference.get(), R.mipmap.playing,playImage);
                }
            }else if(MqService.UPDATA_ID.equals(action)){//换歌了

                EventBus.getDefault().post(new Event.PlayEvent(1));//正在播放刷新

            }


        }
    }

    private void autoDown(){
        if(NetStateUtils.isWifi(this)){
            SQLite.select()
                    .from(TasksManagerModel.class)
                    .orderBy(TasksManagerModel_Table.Idd,true)
                    .async()
                    .queryListResultCallback(new QueryTransaction.QueryResultListCallback<TasksManagerModel>() {
                        @Override
                        public void onListQueryResult(QueryTransaction transaction, @NonNull List<TasksManagerModel> tResult) {
                            NLog.e(NLog.TAGDOWN, "来自MainActivity   SQLite  onListQueryResult 查询成功");
                            if(!CheckUtil.isEmpty(tResult)){
                                NLog.e(NLog.TAGDOWN, "来自MainActivity   SQLite  onListQueryResult 查询成功" + tResult.size());

                                DownManger.addListTask(tResult, new DownManger.ResultCallback() {
                                    @Override
                                    public void onSuccess(Object o) {
                                       // NToast.shortToastBaseApp("自动下载未完成任务");
                                    }

                                    @Override
                                    public void onError(String errString) {

                                    }
                                });

                            }else {

                            }

                        }
                    })
                    .error(new Transaction.Error() {
                        @Override
                        public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
                            NLog.e(NLog.TAGDOWN, "来自MainActivity   SQLite  error"+error.getMessage());
                        }
                    })
                    .execute();
        }
    }

    private void setDelAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("通知栏权限未打开，请在在设置中打开？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    @Override
    protected void onDestroy() {
        UseData.setSTimeing(0);//本次播放停止直接 取消定时
        EventBus.getDefault().unregister(this);//解除订阅
        DownManger.close();
        UserInfoManger.getInstance().clear();
        unregisterReceiver(mPlaybackStatus);
        RichText.recycle();
        super.onDestroy();
    }

}
