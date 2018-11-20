package fm.qian.michael.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xxbm.sbecomlibrary.utils.CheckUtil;

import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import fm.qian.michael.service.MqService;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.utils.NLog;

public class SimpleWidgetProvider extends AppWidgetProvider {

    private static boolean isInUse;
    private ComAll comAll;
    private Bitmap bitmap;

    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, SimpleWidgetProvider.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("harvic:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }

    // 更新所有的 widget
    private synchronized void pushUpdate(final Context context, AppWidgetManager appWidgetManager ) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.simple_control_widget_layout);
        //将按钮与点击事件绑定
        remoteView.setOnClickPendingIntent(R.id.widget_play,getPendingIntent(context, R.id.widget_play));
        remoteView.setOnClickPendingIntent(R.id.widget_pre, getPendingIntent(context, R.id.widget_pre));
        remoteView.setOnClickPendingIntent(R.id.widget_next, getPendingIntent(context, R.id.widget_next));
        remoteView.setOnClickPendingIntent(R.id.widget_image, getPendingIntent(context, R.id.widget_image));

        if(MusicPlayerManger.isPlaying()){
            remoteView.setImageViewResource(R.id.widget_play,R.drawable.widget_pause_selector);
        }else {
            remoteView.setImageViewResource(R.id.widget_play,R.drawable.widget_play_selector);
        }

        if(null != comAll)
        remoteView.setTextViewText(R.id.name_tv,comAll.getTitle());

        if(null != bitmap){
            remoteView.setImageViewBitmap(R.id.widget_image,bitmap);
        }

        // 相当于获得所有本程序创建的appwidget
        ComponentName componentName = new ComponentName(context,SimpleWidgetProvider.class);
        appWidgetManager.updateAppWidget(componentName, remoteView);
    }

    private void glide(final Context context){
        if(null != comAll){
            if(!CheckUtil.isEmpty(comAll.getCover_small())){
                RequestOptions options = new RequestOptions()
                        .error(R.mipmap.logo)
                        .priority(Priority.LOW);
                Glide.with(BaseApplation.getBaseApp())
                        .asBitmap()
                        .apply(options)
                        .load(comAll.getCover_small())
                        .into(new SimpleTarget<Bitmap>(){
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bitmap = resource;
                                pushUpdate(context,AppWidgetManager.getInstance(context));
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {

                                bitmap = null;
                                pushUpdate(context,AppWidgetManager.getInstance(context));

                            }
                        });
            }else {
                bitmap = null;
                pushUpdate(context,AppWidgetManager.getInstance(context));
            }
        }else {
            bitmap = null;
            pushUpdate(context,AppWidgetManager.getInstance(context));
        }
    }

    //当第一个框口小部件第一次被添加到桌面，调用此方法
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        isInUse =true;

    }
    //当最后一个该Widget删除是调用该方法，注意是最后一个
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        isInUse = false;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        if(isInUse)
        pushUpdate(context,appWidgetManager);

    }
    //每删除一次窗口小部件，就调用一次
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    // 接收广播的回调函数
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {
            this.onEnabled(context);
        }
        else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
            this.onDisabled(context);
        }
        if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            switch (buttonId) {
                case R.id.widget_play:
                    NLog.e(NLog.PLAYER,"点击 widget_play");
                    MusicPlayerManger.pOrq();

                break;
                case R.id.widget_pre:
                    NLog.e(NLog.PLAYER,"点击 widget_pre");
                    MusicPlayerManger.up();

                break;
                case R.id.widget_next:
                    NLog.e(NLog.PLAYER,"点击 widget_next");
                    MusicPlayerManger.next();
                break;
                case R.id.widget_image:
                    NLog.e(NLog.PLAYER,"点击 widget_image");

                    Intent nowPlayingIntent = new Intent();
                    //nowPlayingIntent.setAction("com.wm.remusic.LAUNCH_NOW_PLAYING_ACTION");
                    nowPlayingIntent.setComponent(new ComponentName("fm.qian.michael","fm.qian.michael.ui.activity.SplashActivity"));
                    nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(nowPlayingIntent);

                    break;
            }

        }else if(MqService.UPDATA_PAUSE.equals(action)){
            pushUpdate(context,AppWidgetManager.getInstance(context));

        }else if(MqService.UPDATA_ID.equals(action)){

            //String  playid = intent.getStringExtra("PLAYID");

            comAll =  MusicPlayerManger.getCommAll();
            glide(context);

        }else if(MqService.SEND_PROGRESS.equals(action)){
            pushUpdate(context,AppWidgetManager.getInstance(context));
        }
        super.onReceive(context, intent);
    }
}