package fm.qian.michael.utils;


import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/*
 * lv   2018/9/7
 */
public class LayoutParmsUtils {

    public static ViewGroup.LayoutParams getGroupParms(int hight){
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,hight);
        return params;
    }
    public static ViewGroup.LayoutParams getGroupParmsW(int wight){
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wight, ViewGroup.LayoutParams.WRAP_CONTENT);
        return params;
    }

    public static ViewGroup.LayoutParams getGroupParms(int wight,int hight){
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wight,hight);
        return params;
    }

    public static LinearLayout.LayoutParams getLParms(int hight,int left, int top, int right, int bottom){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,hight);
        params.setMargins(left,top,right,bottom);
        return params;
    }
    public static LinearLayout.LayoutParams getLParms(int weight,int hight,int left, int top, int right, int bottom){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(weight,hight);
        params.setMargins(left,top,right,bottom);
        return params;
    }

    public static RelativeLayout.LayoutParams getRParms(int hight){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,hight);

        return params;
    }

    public static void  setHight(int hight, View view){

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        layoutParams.height = hight;

    }

    public static void  setView(int hight,View view){
        if(null != view){
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(hight,hight);
            view.setLayoutParams(layoutParams);
        }

    }
}
