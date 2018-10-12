package fm.qian.michael.widget.pop;

import android.app.Activity;
import android.view.ViewGroup;

/**
 * Created by 吕 on 2017/12/21.
 */

public class CustomPopuWindConfig {

    private Activity context;

    private boolean isFocusable = true;
    private boolean isOutsideTouchable = true;
    private boolean isTouMing = false;
    private int animation = -1;
    private float aFloat = 0.7f;

    private int with = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int hight = ViewGroup.LayoutParams.WRAP_CONTENT;

    public CustomPopuWindConfig(Activity context) {
        this.context = context;
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public float getaFloat() {
        return aFloat;
    }

    public void setaFloat(float aFloat) {
        this.aFloat = aFloat;
    }

    public boolean isTouMing() {
        return isTouMing;
    }

    public void setTouMing(boolean touMing) {
        isTouMing = touMing;
    }

    public int getWith() {
        return with;
    }

    public void setWith(int with) {
        this.with = with;
    }

    public int getHight() {
        return hight;
    }

    public void setHight(int hight) {
        this.hight = hight;
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
    }

    public boolean isFocusable() {
        return isFocusable;
    }

    public void setFocusable(boolean focusable) {
        isFocusable = focusable;
    }

    public boolean isOutsideTouchable() {
        return isOutsideTouchable;
    }

    public void setOutsideTouchable(boolean outsideTouchable) {
        isOutsideTouchable = outsideTouchable;
    }

    public static class Builder{

        private CustomPopuWindConfig config;

        public Builder(Activity context) {
            this.config = new CustomPopuWindConfig(context);
        }

        public Builder setWith(int with){
            config.setWith(with);
            return this;
        }

        public Builder setHigh(int high){
            config.setHight(high);
            return this;
        }

        public Builder setAnimation(int animation1){
            config.setAnimation(animation1);
            return this;
        }

        public Builder setFocusable(boolean isFocusable){
            config.setFocusable(isFocusable);
            return this;
        }

        public Builder setOutSideTouchable(boolean isOutSize){
            config.setOutsideTouchable(isOutSize);
            return this;
        }

        public Builder setTouMing(boolean is){
            config.setTouMing(is);
            return this;
        }

        public Builder setApha(float f){
            config.setaFloat(f);
            return this;
        }

        public CustomPopuWindConfig build(){

            return config;
        }

    }


}
