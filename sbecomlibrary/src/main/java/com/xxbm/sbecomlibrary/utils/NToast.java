/*
    ShengDao Android Client, NToast
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package com.xxbm.sbecomlibrary.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.xxbm.sbecomlibrary.base.BaseApplation;


public class NToast {

    public static void shortToast( int resId) {
        showToast(BaseApplation.getBaseApp(), resId, Toast.LENGTH_SHORT);
    }
    public static void shortToast(Context context, int resId) {
        showToast(context, resId, Toast.LENGTH_SHORT);
    }


    public static void shortToast(Context context, String text) {
        if (!TextUtils.isEmpty(text) && !"".equals(text.trim())) {
            showToast(context, text, Toast.LENGTH_SHORT);
        }
    }
    public static void shortToastBaseApp(String text) {
        if (!TextUtils.isEmpty(text) && !"".equals(text.trim())) {
            if(BaseApplation.getActivity() != null){
                showToast(BaseApplation.getActivity(), text, Toast.LENGTH_SHORT);
            }else {
                showToast(BaseApplation.getBaseApp(), text, Toast.LENGTH_SHORT);
            }
        }
    }
    public static void shortToastBaseApp(Context context,String text) {
        if (!TextUtils.isEmpty(text) && !"".equals(text.trim())) {
            showToast(context, text, Toast.LENGTH_SHORT);
        }
    }

    public static void shortToastBaseApp( int resId) {
        showToast(BaseApplation.getBaseApp(), resId, Toast.LENGTH_SHORT);
    }

    public static void longToast(Context context, int resId) {
        showToast(context, resId, Toast.LENGTH_LONG);
    }

    public static void longToast(Context context, String text) {
        if (!TextUtils.isEmpty(text) && !"".equals(text.trim())) {
            showToast(context, text, Toast.LENGTH_LONG);
        }
    }

    public static void showToast(Context context, int resId, int duration) {
        if (context == null) {
            return;
        }
        if (context != null && context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        String text = context.getString(resId);
        showToast(context, text, duration);
    }

//    public static void showToast(Context context, String text, int duration) {
//        if (context == null) {
//            return;
//        }
//        if (context != null && context instanceof Activity) {
//            if (((Activity) context).isFinishing()) {
//                return;
//            }
//        }
//        if (!TextUtils.isEmpty(text) && !"".equals(text.trim())) {
//            Toast.makeText(context, text, duration).show();
//        }
//    }

    public static void showToast(Context context, String text, int duration) {
        if (context == null) {
            return;
        }
        if (context != null && context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        if (!TextUtils.isEmpty(text) && !"".equals(text.trim())) {
             com.mic.etoast2.Toast.makeText(context, text, duration).show();
        }
    }

    //设置土司
    public static void customLongShowToast(Context context, String info){
        Toast toast = null;
        if (toast==null) {
            toast = Toast.makeText(context, info, Toast.LENGTH_LONG);
           // LinearLayout layout = (LinearLayout) toast.getView();
           // layout.setBackgroundResource(R.color.b_c);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
           // v.setTextColor(ContextCompat.getColor(context,R.color.t_c));
            v.setTextSize(16);
        }else {
            toast.setText(info);
        }
        toast.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0手机以上
            if (((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                return false;
            }
        }

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;

        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
