package com.xxbm.sbecomlibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class SdcardUtil {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    //获取应用根目录/storage/emulated/0
    public static String getStorageDirectory() {
        if (hasSdcard())
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        else
            return Environment.getDataDirectory().getAbsolutePath();
    }

    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static File checkDir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public  static  void checkFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  static String checkPathFile(String path, String filename) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path+ File.separator+filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       // Logger.d("watch file= "+path+File.separator+filename);
        return  path+ File.separator+filename;
    }


   //获取SDCard/Android/data/你的应用包名/cache/目录
    public static File getCacheDirectory(Context var0) {
        return getCacheDirectory(var0, true);
    }

    public static File getCacheDirectory(Context var0, boolean var1) {
        File var2 = null;
        if(var1 && "mounted".equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(var0)) {
            var2 = getExternalCacheDir(var0);
        }

        if(var2 == null) {
            var2 = var0.getCacheDir();
        }

        if(var2 == null) {
            String var3 = "/data/data/" + var0.getPackageName() + "/cache/";
          //  L.w("Can\'t define system cache directory! \'%s\' will be used.", new Object[]{var3});
            var2 = new File(var3);
        }

        return var2;
    }
    private static File getExternalCacheDir(Context var0) {
        File var1 = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File var2 = new File(new File(var1, var0.getPackageName()), "cache");
        if(!var2.exists()) {
            if(!var2.mkdirs()) {
              //  L.w("Unable to create external cache directory", new Object[0]);
                return null;
            }

            try {
                (new File(var2, ".nomedia")).createNewFile();
            } catch (IOException var4) {
               // L.i("Can\'t create \".nomedia\" file in application external cache directory", new Object[0]);
            }
        }

        return var2;
    }

    private static boolean hasExternalStoragePermission(Context var0) {
        int var1 = var0.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return var1 == 0;
    }

    //前者获取到的就是 /sdcard/Android/data/<application package>/cache 这个路径
    // ，而后者获取到的是 /data/data/<application package>/cache 这个路径。
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
    //前者获取到的就是 /sdcard/Android/data/<application package>/file 这个路径
    // ，而后者获取到的是 /data/data/<application package>/file 这个路径。
    private static String defaultSaveRootPath;

    public static String getDiskFileDir(Context context, String uniqueName) { //连续访问文件 耗时严重
        if (!CheckUtil.isEmpty(defaultSaveRootPath)) {

            // NLog.e(NLog.TAGOther,defaultSaveRootPath);

            return defaultSaveRootPath;
        }
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            if(null != context.getExternalFilesDir(uniqueName)){
                cachePath = context.getExternalFilesDir(uniqueName).getAbsolutePath();

                defaultSaveRootPath = cachePath;
                return cachePath;
            }else {
                return null;
            }

        } else {

            cachePath = context.getFilesDir().getAbsolutePath();
        }

        File cacheDir = new File(cachePath + File.separator + uniqueName);

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        defaultSaveRootPath = cacheDir.getAbsolutePath();

        return cacheDir.getAbsolutePath();
    }
// FIXME: 2018/10/31
    private static String defaultDownRoot; ///sdcard/Android/data/<application package>/file/down
    public static String getDiskFileDir(Context context) { //连续访问文件 耗时严重

        if(!CheckUtil.isEmpty(defaultDownRoot)){
            return defaultDownRoot;
        }
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {

                cachePath = context.getExternalFilesDir("down").getAbsolutePath();

        } else {

            cachePath = context.getFilesDir().getAbsolutePath() + File.separator + "down";

        }

        defaultDownRoot = cachePath;

        return cachePath;
    }

//
    public static String getDefaultSaveRootPath(Context context) {

        if (context.getExternalCacheDir() == null) {
            return Environment.getDownloadCacheDirectory().getAbsolutePath();
        } else {
            return context.getExternalCacheDir().getAbsolutePath();
        }
    }

    //取到当前应用程序的版本号
    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            NLog.e(NLog.TAGOther,"AppVersion--->"+info.versionCode);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }




    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
