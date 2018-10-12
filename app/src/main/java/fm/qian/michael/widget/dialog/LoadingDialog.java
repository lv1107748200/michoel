package fm.qian.michael.widget.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;


import fm.qian.michael.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.qian.michael.base.BaseApplation;

/*
 * lv   2018/8/9
 */
public class LoadingDialog {

    private Context mContext;
    private Dialog mDialog;
    private String text;
    private boolean canOut = false;
    private boolean isShow = true;
    private boolean isPro = true;

    private int gravity = -1;
    private int animationResId = -1;

    private DiaLogCallBack callBack;


    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv)
    TextView tvDialog;

    @OnClick({})
    public void OnClick(View view){
        switch(view.getId()){

        }
    }



        public LoadingDialog(Context mContext) {
            this.mContext = mContext;
        }

        public LoadingDialog setCallBack(DiaLogCallBack callBack) {
            this.callBack = callBack;
            return this;
        }

        public LoadingDialog setText(String text){
            this.text = text;
            return this;
        }
        public LoadingDialog setCanOut(boolean is){

            this.canOut = is;

            return this;
        }

        public LoadingDialog setIsShow(boolean isShow){
            this.isShow = isShow;
            return this;
        }
        public LoadingDialog setIsPro(boolean isShow){
            this.isPro = isShow;
            return this;
        }

        public LoadingDialog setGravity(int gravity){
            this.gravity = gravity;
            return this;
        }

        public LoadingDialog setAnimationResid(int animationResId){
            this.animationResId = animationResId;
            return this;
        }

        public LoadingDialog creatDialog(){

            mDialog = new Dialog(mContext, R.style.dialog);

            View view = LayoutInflater.from(mContext).inflate(R.layout.loading_dialog_layout, null);

            ButterKnife.bind(this,view);

            mDialog.setContentView(view);
            mDialog.setCanceledOnTouchOutside(canOut);

            Window dialogWindow = mDialog.getWindow();
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值

            if(gravity>0){
                dialogWindow.setGravity(gravity);//设置对话框位置
            }

            if(animationResId > 0){
                dialogWindow.setWindowAnimations(animationResId);
            }


            // params.width = (int) (DisplayUtils.getScreenWidth(mContext) * 0.6); // 宽度设置为屏幕的0.65，根据实际情况调整

            dialogWindow.setAttributes(params);

            if(isShow){
              //  mDialog.show();
            }


            return this;
        }


        public void setTvDialog(String s){
           // tvDialog.startAnimation(AnimationUtils.loadAnimation(mContext,android.R.anim.fade_in));
           // tvDialog.setVisibility(View.VISIBLE);
            tvDialog.setText(s);
        }

        public void ShanChu(String s){
                progressBar.setVisibility(View.VISIBLE);
                tvDialog.startAnimation(AnimationUtils.loadAnimation(mContext,android.R.anim.fade_in));
                tvDialog.setVisibility(View.VISIBLE);
                tvDialog.setText(s);

        }

        public void ShanChuOr(boolean is,String s){
            if(is){
                progressBar.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(mContext,android.R.anim.fade_in);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        diss();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                tvDialog.startAnimation(animation);
                tvDialog.setVisibility(View.VISIBLE);
                tvDialog.setText(s);
            }else {
                progressBar.setVisibility(View.GONE);
                tvDialog.startAnimation(AnimationUtils.loadAnimation(mContext,android.R.anim.fade_in));
                tvDialog.setVisibility(View.VISIBLE);
                tvDialog.setText(s);
            }

        }


        public void show(String s){
            creatDialog();
            mDialog.show();
            setTvDialog(s);
        }

        public void diss(){
            if(null != mDialog){
                if(mDialog.isShowing()){
                    mDialog.dismiss();
                }
            }
        }


        public interface DiaLogCallBack{
            void BFCallBack();
            void SCCallBack();
        }


}


