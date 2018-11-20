/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package fm.qian.michael.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.icu.util.RangeValueIterator;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.db.UseData;
import com.xxbm.sbecomlibrary.net.entry.response.Base;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import com.xxbm.sbecomlibrary.utils.NToast;

import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.activity.WebParticularsActivity;
import fm.qian.michael.ui.activity.WebTBSParticularsActivity;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.HeadGroupTopActivity;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.wxapi.Constants;


public class CommonUtils {


    public static final String MUSIC_ONLY_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1"
            + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {

        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) BaseApplation.getBaseApp().getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            @SuppressLint("MissingPermission")
            String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
        }



        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 获取apk的版本号 currentVersionCode
     *
     * @param ctx
     * @return
     */
    public static int getAPPVersionCode(Context ctx) {
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            currentVersionCode = info.versionCode; // 版本号
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }
    public static String getAPPVersionName(Context ctx) {
        String currentVersionName = "";
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            currentVersionName= info.versionName; // 版本名
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentVersionName;
    }

    public static String getDeviceInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("MODEL = " + Build.MODEL + "\n");
        builder.append("PRODUCT = " + Build.PRODUCT + "\n");
        builder.append("TAGS = " + Build.TAGS + "\n");
        builder.append("CPU_ABI" + Build.CPU_ABI + "\n");
        builder.append("BOARD = " + Build.BOARD + "\n");
        builder.append("BRAND = " + Build.BRAND + "\n");
        builder.append("DEVICE = " + Build.DEVICE + "\n");
        builder.append("DISPLAY = " + Build.DISPLAY + "\n");
        builder.append("ID = " + Build.ID + "\n");
        builder.append("VERSION.RELEASE = " + Build.VERSION.RELEASE + "\n");
        builder.append("Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT + "\n");
        builder.append("VERSION.BASE_OS = " + Build.VERSION.BASE_OS + "\n");
        builder.append("Build.VERSION.SDK = " + Build.VERSION.SDK + "\n");
        builder.append("APP.VERSION = " + getAPPVersionCode(BaseApplation.getBaseApp()) + "\n");
        builder.append("\n" + "log:" + "\n");

        return builder.toString();
    }

    /**
     * 以文本格式发送邮件
     * @param title 待发送的邮件的信息
     */
    public static boolean sendTextMail(String title , String content)
    {

        return false;
    }


    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    // 检测MIUI

    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

//    public static boolean isMIUI() {
//        try {
//            final BuildProperties prop = BuildProperties.newInstance();
//            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
//                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
//                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
//        } catch (final IOException e) {
//            return false;
//        }
//    }


// 检测Flyme

    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }


    public static Uri getAlbumArtUri(long paramInt) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), paramInt);
    }


    public static final String makeLabel(final Context context, final int pluralInt,
                                         final int number) {
        return context.getResources().getQuantityString(pluralInt, number, number);
    }

//    public static final String makeShortTimeString(final Context context, long secs) {
//        long hours, mins;
//
//        hours = secs / 3600;
//        secs %= 3600;
//        mins = secs / 60;
//        secs %= 60;
//
//        final String durationFormat = context.getResources().getString(
//                hours == 0 ? "durationformatshort" : R.string.durationformatlong);
//        return String.format(durationFormat, hours, mins, secs);
//    }

    public static int getActionBarHeight(Context context) {
        int mActionBarHeight;
        TypedValue mTypedValue = new TypedValue();

        context.getTheme().resolveAttribute(R.attr.actionBarSize, mTypedValue, true);

        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, context.getResources().getDisplayMetrics());

        return mActionBarHeight;
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }}

    public static <T> T copy(Parcelable input) {
        Parcel parcel = null;

        try {
            parcel = Parcel.obtain();
            parcel.writeParcelable(input, 0);

            parcel.setDataPosition(0);
            return parcel.readParcelable(input.getClass().getClassLoader());
        } finally {
            parcel.recycle();
        }
    }



//    public static boolean hasEffectsPanel(final Activity activity) {
//        final PackageManager packageManager = activity.getPackageManager();
//        return packageManager.resolveActivity(createEffectsIntent(),
//                PackageManager.MATCH_DEFAULT_ONLY) != null;
//    }

//    public static Intent createEffectsIntent() {
//        final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
//        effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicPlayer.getAudioSessionId());
//        return effects;
//    }

    public static int getBlackWhiteColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness >= 0.5) {
            return Color.WHITE;
        } else return Color.BLACK;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getRandom(){
        Random random = new Random();
        return random.nextInt(1000000);
    }

    public static String setString(String s,String Repase,String what){

        s = s.replaceAll(Repase,what);

        return s;
    }

    public static String getNewContent(String htmltext) {

        Document doc = Jsoup.parse(htmltext);
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("width","100%").attr("height","auto");
        }

        return doc.toString();
    }


    public static boolean setIntent(Intent intent,Context context, Base base){

        String tid = base.getTid();

        NLog.e(NLog.TAGOther,tid);

        if(CheckUtil.isEmpty(tid)){
            tid = "";
        }

        switch (tid){
            case GlobalVariable.ONE://音频
                ComAll comAll = new ComAll();
                comAll.setId(base.getZid());
                CommonUtils.getIntent(context, Arrays.asList(comAll),0, GlobalVariable.SIX);

                break;
            case GlobalVariable.TWO://视频
                intent.setClass(context, WebTBSParticularsActivity.class);
                intent.putExtra(WebTBSParticularsActivity.WEBTYPE,tid);
                intent.putExtra(WebTBSParticularsActivity.WEBID,base.getZid());
                context.startActivity(intent);
                break;
            case GlobalVariable.THREE://文章
                intent.setClass(context, WebTBSParticularsActivity.class);
                intent.putExtra(WebTBSParticularsActivity.WEBTYPE,tid);
                intent.putExtra(WebTBSParticularsActivity.WEBID,base.getZid());
                context.startActivity(intent);
                break;
            case GlobalVariable.FOUR://专辑
                intent.setClass(context, HeadGroupActivity.class);
                intent.putExtra(HeadGroupActivity.HEADGROUP,base.getZid());
                context.startActivity(intent);
                break;
            case GlobalVariable.FIVE://专题
                intent.setClass(context, HeadGroupTopActivity.class);
                intent.putExtra(HeadGroupTopActivity.HEADGROUPOTHER,base.getZid());
                context.startActivity(intent);
                break;
            default:
                EventBus.getDefault().post(new Event.MainActivityEvent(2));
            break;
        }


        return true;
    }


    public static Intent getIntent(final Context context,final List<ComAll> list,final int num,String what){

       final Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context,PlayActivity.class);

        if(!CheckUtil.isEmpty(what)){
            UseData.setInit(what);
        }


//        if(MusicPlayerManger.isPlaying())
//            MusicPlayerManger.pause();//暂停

//        MusicPlayerManger.updata(list,num);
//        MusicPlayerManger.play();


      //  MusicPlayerManger.synthesizeMake(list,num);


        if(MusicPlayerManger.isNull()){
           MusicPlayerManger. bindStartByContext(BaseApplation.getBaseApp(), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    MusicPlayerManger.updata(list,num);
                    context.startActivity(intent);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            });

        }else {
            MusicPlayerManger.updata(list,num);
            context.startActivity(intent);
        }
        return intent;
    }

    public static void getWebIntent(Context context,String what,String id){

        Intent intent = new Intent(context, WebTBSParticularsActivity.class);
        intent.putExtra(WebTBSParticularsActivity.WEBTYPE, what);
        intent.putExtra(WebTBSParticularsActivity.WEBID,id);

        context.startActivity(intent);
    }

    public static String setJoint(List<ComAll> comAllList){

        StringBuffer stringBuffer = new StringBuffer();

        for(int i = 0; i< comAllList.size(); i++){
            stringBuffer.append(comAllList.get(i).getId());
            stringBuffer.append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);

      return stringBuffer.toString();
    }

    public static String getNumberIndex(int what){
        String num = what +"";
        if(num.length() == 1){
            num = "0"+num;
        }

        return num;
    }


    public static void weixinLogin(Context context,String type,boolean isfinsh){
        Constants.wx_api = WXAPIFactory.createWXAPI(context, Constants.APP_ID, true);
        Constants.wx_api.registerApp(Constants.APP_ID);
        Constants.type = type;


        if (!Constants.wx_api.isWXAppInstalled()) {
            NToast.shortToastBaseApp("未安装微信");
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            Constants.wx_api.sendReq(req);

            if(isfinsh){
                Activity activity = (Activity) context;
                activity.finish();
            }
        }

    }

    public static String getSHA1Signature(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName()
                    , PackageManager.GET_SIGNATURES);

            byte[] cert = info.signatures[0].toByteArray();

            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static  String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
