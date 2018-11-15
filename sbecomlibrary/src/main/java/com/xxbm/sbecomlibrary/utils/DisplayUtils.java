package com.xxbm.sbecomlibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.xxbm.sbecomlibrary.R;


/**
 * Created by 吕 on 2017/6/20.
 */

public class DisplayUtils {
    //获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
    //获取屏幕的高度
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public static int getDimen(Context context, @DimenRes int id){
        return context.getResources().getDimensionPixelOffset(id);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context,float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip( Context context,float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal)

    {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,

                spVal,context.getResources().getDisplayMetrics());

    }

    /**
     * px转sp
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal)

    {

        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);

    }


    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public static int getNavigationBarHeight(Context context) {

        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            //获取NavigationBar的高度
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        }
        else{
            return 0;
        }
    }
    public static int getDisplayHeight(Context paramContext) {
        return paramContext.getResources().getDisplayMetrics().heightPixels;
    }
    public static int getDisplayWidth(Context paramContext) {
        return paramContext.getResources().getDisplayMetrics().widthPixels;
    }

    public static int[] getViewLocation(View paramView, Activity paramActivity) {
        Rect frame = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int[] ctrlLocation = new int[2];
        paramView.getLocationOnScreen(ctrlLocation);
        int ctrlScreenY = ctrlLocation[1];
        int ctrlViewY = ctrlScreenY - statusBarHeight;
        ctrlLocation[1] = ctrlViewY;
        return ctrlLocation;
    }

    /**
     * 设置页面的透明度
     * @param bgAlpha 1表示不透明
     */
    public static   void setBackgroundAlpha( float bgAlpha,Activity context) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        context.getWindow().setAttributes(lp);
    }


    /**
     * @param ge
     * @param pssding
     * @param ether
     * @return
     */
    public static int getWideM(Context context,int ge,int pssding,int ether){

      int weid = getDisplayWidth(context)  - ((ge-1)*pssding + 2 * ether);

      return weid/ge;

    }

    public static int getWideP(Context context,int ge,int pssding,int ether){

        int weid = getDisplayWidth(context)  - ((ge-1)*pssding + 2 * ether);

        return weid/ge;

    }
    private static int wight = -1;

//    public static int ImageHight3( Context context ){
//
//        if(wight<0){
//            wight = DisplayUtils.getWideP(context,3,0
//                    ,DisplayUtils.getDimen(context,R.dimen.margin_11));
//        }
//
//        return wight;
//    }
    public static int imageHight1(Context context,int ge,int pssding,int ether){

        int weid = getDisplayWidth(context)  - ((ge-1)*pssding + 2 * ether);

        return weid/ge;

    }
}
