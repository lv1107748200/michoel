package fm.qian.michael.widget.pop;


import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.ui.activity.LoginActivity;
import fm.qian.michael.utils.CommonUtils;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.wxapi.Constants;

import static fm.qian.michael.common.GlobalVariable.THREE;
import static fm.qian.michael.common.GlobalVariable.TWO;

/*
 * lv   2018/9/26 微信登陆
 */
public class PopLoginWindow extends BasePopupWindow {
    private Context context;


    @OnClick({R.id.del_image, R.id.weixin_login_layout,R.id.layoutZC,R.id.layoutDL})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.del_image://取消
                dismiss();
                break;
            case R.id.weixin_login_layout://微信登陆
                if(true){
                    CommonUtils.weixinLogin(context, GlobalVariable.ZERO,false);
                }
                dismiss();
                break;
            case R.id.layoutZC://注册
                if(true){
                    Intent intent = new Intent(context,LoginActivity.class);
                    intent.putExtra(LoginActivity.LOGIN,TWO);
                    context.startActivity(intent);
                }
                dismiss();
                break;
            case R.id.layoutDL://登陆
                if(true){
                    Intent intent = new Intent(context,LoginActivity.class);
                    intent.putExtra(LoginActivity.LOGIN,THREE);
                    context.startActivity(intent);
                }
                dismiss();
                break;
        }
    }

    public PopLoginWindow(CustomPopuWindConfig config,Context context) {
        super(config);
        this.context = context;
    }

    @Override
    public View getView(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.pop_wx_login,
                null,false);

        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onDiss() {

    }
    @Override
    public void show(View parent) {
        super.show(parent);

        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, Gravity.CENTER,0, 0);

        } else {

            this.dismiss();

        }
    }


}
