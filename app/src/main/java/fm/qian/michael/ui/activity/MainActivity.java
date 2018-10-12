package fm.qian.michael.ui.activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hr.bclibrary.utils.CheckUtil;

import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.base.activity.BaseExitActivity;
import fm.qian.michael.base.activity.PermissionCallback;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.db.UseData;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.service.MqService;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.ui.activity.dim.SearchActivity;
import fm.qian.michael.ui.adapter.MainAdapter;
import fm.qian.michael.ui.fragment.ArticleFragment;
import fm.qian.michael.ui.fragment.CategoryFragment;
import fm.qian.michael.ui.fragment.FindFragment;
import fm.qian.michael.ui.fragment.MyFragment;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NToast;
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
import fm.qian.michael.base.activity.BaseExitActivity;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.ui.activity.dim.SearchActivity;
import fm.qian.michael.ui.adapter.MainAdapter;
import fm.qian.michael.ui.fragment.ArticleFragment;
import fm.qian.michael.ui.fragment.CategoryFragment;
import fm.qian.michael.ui.fragment.FindFragment;
import fm.qian.michael.ui.fragment.MyFragment;
import fm.qian.michael.widget.broadcast.NetworkConnectChangedReceiver;
import fm.qian.michael.widget.custom.BottomBarLayout;
import fm.qian.michael.widget.custom.XCViewPager;
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

    @BindView(R.id.status_bar)
    View statusBar;

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

    @Override
    public void initTitle() {
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

        setStatusBar(statusBar);

        setPermission();
        // setBroadcast(this);

    }

    @Override
    public void initData() {

        List<BottomBarLayout.DataList> dataLists = new ArrayList<>();
        int[] strings = {fm.qian.michael.R.string.发现, fm.qian.michael.R.string.分类, fm.qian.michael.R.string.文章, fm.qian.michael.R.string.我的};
        int[] stringId = {fm.qian.michael.R.drawable.selector_find,
                fm.qian.michael.R.drawable.selector_category,
                fm.qian.michael.R.drawable.selector_article,
                fm.qian.michael.R.drawable.selector_my};

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


    }

    //点击安全企业
    @Override
    public void onSafetyEnterprise() {

        // NLog.e(NLog.TAG,"onSafetyEnterprise");
    }

    @Override
    public void onBottomTab(int num) {

    }
    //网络状态广播
    @Override
    public void setState(boolean is) {
        if(is){
            NToast.shortToast(this,"网络连接！");
        }else {
            NToast.shortToast(this,"无网络连接！");
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

                EventBus.getDefault().post(new Event.PlayEvent());//正在播放刷新

            }


        }
    }

    @Override
    protected void onDestroy() {
        UseData.setSTimeing(0);//本次播放停止直接 取消定时
        EventBus.getDefault().unregister(this);//解除订阅
        DownManger.close();
        UserInfoManger.getInstance().clear();
        unregisterReceiver(mPlaybackStatus);
        super.onDestroy();
    }

}
