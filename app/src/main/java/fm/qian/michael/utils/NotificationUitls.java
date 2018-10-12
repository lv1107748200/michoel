package fm.qian.michael.utils;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import com.hr.bclibrary.utils.CheckUtil;

import fm.qian.michael.ui.activity.MainActivity;

/*
 * lv   2018/9/14
 */
public class NotificationUitls {
    /**
     * 显示一个普通的通知
     *
     * @param nfData 上下文
     */
    public static void showNotification(NFData nfData) {
        if(CheckUtil.isEmpty(nfData))
            return;

        Notification notification = new NotificationCompat.Builder(nfData.context,"name")
                /**设置通知左边的大图标**/
                .setLargeIcon(nfData.bitmap)
                /**设置通知右边的小图标**/
                .setSmallIcon(fm.qian.michael.R.mipmap.logo)
                /**通知首次出现在通知栏，带上升动画效果的**/
                .setTicker(nfData.name + "下载完成")
                /**设置通知的标题**/
                .setContentTitle("下载完成")
                /**设置通知的内容**/
                .setContentText(nfData.name+"下载完成")
                /**通知产生的时间，会在通知信息里显示**/
                .setWhen(System.currentTimeMillis())
                /**设置该通知优先级**/
                .setPriority(Notification.PRIORITY_DEFAULT)
                /**设置这个标志当用户单击面板就可以让通知将自动取消**/
                .setAutoCancel(true)
                /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
                .setOngoing(false)
                /**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：**/
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentIntent(PendingIntent.getActivity(nfData.context, 1, new Intent(nfData.context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();

        NotificationManager notificationManager = (NotificationManager) nfData.context.getSystemService(nfData.context.NOTIFICATION_SERVICE);
        /**发起通知**/
        notificationManager.notify(0, notification);
    }

    public static class NFData{
        public Context context;
        public String name;
        public Bitmap bitmap;

        public NFData(Context context, String name, Bitmap bitmap) {
            this.context = context;
            this.name = name;
            this.bitmap = bitmap;
        }
    }
}
