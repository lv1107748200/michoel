package fm.qian.michael.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.widget.RemoteViews;



import fm.qian.michael.R;

public class SimpleWidgetProvider extends AppWidgetProvider {

    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, SimpleWidgetProvider.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("harvic:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }

    // 更新所有的 widget
    private synchronized void pushUpdate(final Context context, AppWidgetManager appWidgetManager , boolean updateProgress) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.simple_control_widget_layout);
        //将按钮与点击事件绑定
        remoteView.setOnClickPendingIntent(R.id.widget_play,getPendingIntent(context, R.id.widget_play));
        remoteView.setOnClickPendingIntent(R.id.widget_pre, getPendingIntent(context, R.id.widget_pre));
        remoteView.setOnClickPendingIntent(R.id.widget_next, getPendingIntent(context, R.id.widget_next));

        // 相当于获得所有本程序创建的appwidget
        ComponentName componentName = new ComponentName(context,SimpleWidgetProvider.class);
        appWidgetManager.updateAppWidget(componentName, remoteView);
    }

    @Override
    public void onEnabled(Context context) {

        super.onEnabled(context);




    }
    //当最后一个该Widget删除是调用该方法，注意是最后一个
    @Override
    public void onDisabled(Context context) {

        super.onDisabled(context);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {



    }

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

                break;
                case R.id.widget_pre:

                break;
                case R.id.widget_next:

                break;
            }

        }
        super.onReceive(context, intent);
    }
}