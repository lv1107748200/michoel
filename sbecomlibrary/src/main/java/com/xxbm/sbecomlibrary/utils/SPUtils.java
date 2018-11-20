package com.xxbm.sbecomlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.content.SharedPreferencesCompat.EditorCompat;

import com.xxbm.sbecomlibrary.base.BaseApplation;
import com.xxbm.sbecomlibrary.com.UserInforConfig;

import java.util.Map;



/**
 * Created by 吕 on 2017/10/26.
 */

public class SPUtils {

    public static String FILLNAME = "userInfoConfig";

    private volatile static SharedPreferences sp;


    public static void init ( Context context ){
        if(sp == null){
            synchronized (SPUtils.class) {
                if(sp == null){
                    sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
                }
            }
        }
    }

    /**
     * 存入某个key对应的value值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, Object value) {
        if(null == sp){
            sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        Editor edit = sp.edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        }
       EditorCompat.getInstance().apply(edit);
    }

    public static void putString(UserInforConfig key, String value) {
        if(null == sp){
            sp = BaseApplation.getBaseApp().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }

        Editor edit = sp.edit();
        edit.putString(key.getName(), value);
        EditorCompat.getInstance().apply(edit);
    }
    public static void putString(UserInforConfig key, String value, boolean isTong) {
        if(null == sp){
            sp = BaseApplation.getBaseApp().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }

        Editor edit = sp.edit();
        edit.putString(key.getName(), value);
        if(isTong){
            edit.commit();
        }else {
            edit.apply();
        }

    }
    public static String getString(UserInforConfig key, String defValue) {
        if(null == sp){
            sp = BaseApplation.getBaseApp().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        return sp.getString(key.getName(), defValue);
    }

    public static void putInt(UserInforConfig key, int value) {
        if(null == sp){
            sp = BaseApplation.getBaseApp().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        Editor edit = sp.edit();
        edit.putInt(key.getName(), value);
        edit.apply();
    }

    public static void putInt(Context context,UserInforConfig key, int value,boolean isTong) {

        SharedPreferences sp = null;
        if(null == sp){
            sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        Editor edit = sp.edit();
        edit.putInt(key.getName(), value);
        if(isTong){
            edit.commit();
        }else {
            edit.apply();
        }

    }
    public static int getInt(UserInforConfig key, int defValue) {
        if(null == sp){
            sp = BaseApplation.getBaseApp().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        return sp.getInt(key.getName(), defValue);
    }

    /**
     * 得到某个key对应的值
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object get(Context context, String key, Object defValue) {
        if(null == sp){
            sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        if (defValue instanceof String) {
            return sp.getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            return sp.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            return sp.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Long) {
            return sp.getLong(key, (Long) defValue);
        }
        return null;
    }

    /**
     * 返回所有数据
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        if(null == sp){
            sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        return sp.getAll();
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        if(null == sp){
            sp = context.getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        Editor edit = sp.edit();
        edit.remove(key);
        EditorCompat.getInstance().apply(edit);
    }
    public static void remove( UserInforConfig key) {
        if(null == sp){
            sp = BaseApplation.getBaseApp().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        Editor edit = sp.edit();
        edit.remove(key.getName());
        EditorCompat.getInstance().apply(edit);
    }

    /**
     * 清除所有内容
     *
     * @param
     */
    public static void clear() {
        if(null == sp){
            sp = BaseApplation.getBaseApp().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        Editor edit = sp.edit();
        edit.clear();
        EditorCompat.getInstance().apply(edit);
    }

    public static void clearLogin(){
        if(null == sp){
            sp = BaseApplation.getBaseApp().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        Editor edit = sp.edit();

        edit.remove(UserInforConfig.USERNAME.getName());
        edit.remove(UserInforConfig.USERSESSIONKEY.getName());

        edit.commit();
    }
}
