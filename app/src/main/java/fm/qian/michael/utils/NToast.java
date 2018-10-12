/*
    ShengDao Android Client, NToast
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package fm.qian.michael.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import fm.qian.michael.base.BaseApplation;

import fm.qian.michael.base.BaseApplation;


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
    public static void shortToastBaseApp( String text) {
        if (!TextUtils.isEmpty(text) && !"".equals(text.trim())) {
            showToast(BaseApplation.getBaseApp(), text, Toast.LENGTH_SHORT);
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
            Toast.makeText(context, text, duration).show();
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
}
