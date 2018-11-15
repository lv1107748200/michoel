package fm.qian.michael.ui.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hr.bclibrary.utils.CheckUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.service.MqService;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.custom.SelectableRoundedImageView;
import fm.qian.michael.widget.custom.SildingFinishLayout;
import fm.qian.michael.widget.single.DownManger;

import static fm.qian.michael.service.MqService.CMDNAME;
import static fm.qian.michael.service.MqService.CMDNOTIF;

/*
 * lv   2018/10/12
 */
public class LockActivity extends BaseActivity {

    private PlaybackStatus mPlaybackStatus;

    private String id;

    private ComAll comAll;

    @BindView(R.id.statu_view)
    View statu_view;
    @BindView(R.id.item_image)
    SelectableRoundedImageView item_image;
    @BindView(R.id.lock_root)
    SildingFinishLayout lock_root;
    @BindView(R.id.lock_music_play)
    ImageView lock_music_play;

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_zhuozhe)
    TextView tv_zhuozhe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        super.onCreate(savedInstanceState);

       // this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

//
//    @Override
//    public void onBackPressed() {
//        // do nothing
//    }


    @Override
    public boolean isStatusBar() {
        return true;
    }

    @Override
    public void initTitle() {
        super.initTitle();
        setStatusBar(statu_view);

        lock_root.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {

            @Override
            public void onSildingFinish() {
                finish();
            }
        });
        lock_root.setTouchView(getWindow().getDecorView());
    }

    @OnClick({R.id.lock_music_pre,R.id.lock_music_play,R.id.lock_music_next})
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.lock_music_play:
                MusicPlayerManger.pOrq();
                break;
            case R.id.lock_music_next:
                MusicPlayerManger.next();
                break;
            case R.id.lock_music_pre:
                MusicPlayerManger.up();
                break;
        }

    }
    @Override
    public int getLayout() {
        return R.layout.activity_lock;
    }

    @Override
    public void initData() {
        super.initData();

        comAll = MusicPlayerManger.getCommAll();

        if(null != comAll){
            id = comAll.getId();
            //autio();
            setView();
        }

        if(MusicPlayerManger.isPlaying()){
            lock_music_play.setSelected(true);
        }

        setInit();
    }

    private void setView(){
       // GlideUtil.setGlideImage(LockActivity.this,comAll.getCover(),item_image);
        DownManger.setImageView(item_image,comAll.getCover(),this);
        tv_title.setText(comAll.getTitle());

        tv_zhuozhe.setText(comAll.getBroad());
    }


    private void autio(){
        baseService.audio(id, "", "", new HttpCallback<ComAll, BaseDataResponse<ComAll>>() {
            @Override
            public void onError(HttpException e) {

                NToast.shortToastBaseApp(e.getMsg());

            }

            @Override
            public void onSuccess(ComAll comAll) {

                LockActivity.this.comAll = comAll;

                GlideUtil.setGlideImage(LockActivity.this,comAll.getCover(),item_image);

                tv_title.setText(comAll.getTitle());

                tv_zhuozhe.setText(comAll.getBroad());

            }
        },LockActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }


    private void setInit(){
        mPlaybackStatus = new PlaybackStatus(this);
        IntentFilter f = new IntentFilter();
        f.addAction(MqService.MUSIC_LODING);//音乐正在加载
        f.addAction(MqService.SEND_PROGRESS);//开始播放获取进度
        f.addAction(MqService.UPDATA_ID);//换歌了
        f.addAction(MqService.UPDATA_PAUSE);//暂停
        registerReceiver(mPlaybackStatus, new IntentFilter(f));
    }


    private  class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<LockActivity> mReference;


        public PlaybackStatus(final LockActivity activity) {
            mReference = new WeakReference<>(activity);
        }


        @Override
        public void onReceive(final Context context, final Intent intent) {
            if(null != mReference.get())
                mReference.get().handleCommandIntent(intent);

        }
    }
    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = MqService.SERVICECMD.equals(action) ? intent.getStringExtra(CMDNAME) : null;

        //    MLog.E( "来自 PlayActivity 广播 : action = " + action + ", command = " + command);
        if (MqService.UPDATA_PAUSE.equals(action)) {
            lock_music_play.setSelected(false);
        }else if(MqService.SEND_PROGRESS.equals(action)){

            lock_music_play.setSelected(true);

        }else if(MqService.UPDATA_ID.equals(action)){
            String   duration = intent.getStringExtra("PLAYID");

            id = duration;
            if(!CheckUtil.isEmpty(id)){
                comAll = MusicPlayerManger.getCommAll();
                setView();
               // autio();
            }

        }else if(MqService.MUSIC_LODING.equals(action)){

        }else if(MqService.PAUSE_ACTION_APP.equals(action)){ //定时器刷新

        }
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(mPlaybackStatus);

        Intent startIntent = new Intent(MqService.LOCK_SCREEN);
        startIntent.putExtra("islock",false);
        sendBroadcast(startIntent);

        super.onDestroy();
    }
}
