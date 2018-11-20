package fm.qian.michael.widget.pop;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.xxbm.sbecomlibrary.utils.DisplayUtils;


/**
 * Created by 吕 on 2017/12/21.
 */

public abstract  class BasePopupWindow extends PopupWindow {

    private CustomPopuWindConfig config;

    public BasePopupWindow(CustomPopuWindConfig config) {
        super(config.getContext());
        this.config = config;
        init();
    }

    public void init(){

        setContentView(getView(config.getContext()));
        // 设置SelectPicPopupWindow弹出窗体的宽
        setWidth(config.getWith());
        // 设置SelectPicPopupWindow弹出窗体的高
        setHeight(config.getHight());
        // 设置SelectPicPopupWindow弹出窗体可点击
        setFocusable(config.isFocusable());
        setOutsideTouchable(config.isOutsideTouchable());


        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        if(config.getAnimation() > -1){
            this.setAnimationStyle(config.getAnimation());
        }

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {

                if(!config.isTouMing()){
                    DisplayUtils.setBackgroundAlpha(1.0f, config.getContext());
                }

                onDiss();
            }
        });


    }

    public abstract View getView(Context context );

    public abstract void onDiss();


    public  void show(View parent){

        if(!config.isTouMing())
        DisplayUtils.setBackgroundAlpha(config.getaFloat(), config.getContext());

    }
}
