package fm.qian.michael.ui.activity;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.entry.response.Ver;
import com.xxbm.sbecomlibrary.net.http.HttpCallback;
import com.xxbm.sbecomlibrary.net.http.HttpException;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.fragment.MyFragment;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.NLog;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.utils.SPUtils;
import fm.qian.michael.widget.custom.SelectableRoundedImageView;
import fm.qian.michael.widget.single.UserInfoManger;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/*
 * lv   2018/9/26
 */
public class SetActivity extends BaseIntensifyActivity {


    @BindView(R.id.up_image)
    ImageView up_image;
    @BindView(R.id.tv_banbenhao)
    TextView tv_banbenhao;
    @BindView(R.id.item_image)
    SelectableRoundedImageView item_image;
    @OnClick({R.id.out_login_layout
    ,R.id.benbenhao_layout,R.id.guanyu_layout,R.id.lianxi_layout})
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.base_left_layout:
                finish();
                break;
            case R.id.base_right_layout:
                break;
            case R.id.out_login_layout:
                if(!isLogin()){
                    return;
                }
                setDelAlertDialog();
                break;
            case R.id.benbenhao_layout:

                goToMarket(this,"fm.qian.michael");


                break;
            case R.id.guanyu_layout:
                if(true){
                    Intent intent = new Intent(this, WebTBSParticularsActivity.class);
                    intent.putExtra(WebTBSParticularsActivity.WEBTYPE, GlobalVariable.FIVE);
                    startActivity(intent);
                }
                break;

            case R.id.lianxi_layout:
                if(true){
                    Intent intent = new Intent(this, WebTBSParticularsActivity.class);
                    intent.putExtra(WebTBSParticularsActivity.WEBTYPE, GlobalVariable.SIX);
                    startActivity(intent);
                }

                break;



        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_set;
    }

    @Override
    public void initView() {
        super.initView();
        setTitleTv("设置");
        tv_banbenhao.setText(CommonUtils.getAPPVersionName(this));
        Ver();
    }


    private void Ver(){
        baseService.ver(new HttpCallback<Ver, BaseDataResponse<Ver>>() {
            @Override
            public void onError(HttpException e) {
                NLog.e(NLog.TAG,"onError");
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(Ver ver) {
                //NLog.e(NLog.TAG,""+ver.getVer() + ver.getVerint());
               // tv_banbenhao.setText(ver.getVer());


                if(ver.getVerint() == CommonUtils.getAPPVersionCode(SetActivity.this)){
                    up_image.setVisibility(View.GONE);
                }else {
                    up_image.setVisibility(View.VISIBLE);
                }
            }

        },SetActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    private void setDelAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确认要退出吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                UserInfoManger.getInstance().clearLogin();//清空本地数据
                UserInfoManger.getInstance().clear();//清空缓存
                MusicPlayerManger.login(1);//告知播放器

                Intent intent = new Intent(SetActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                EventBus.getDefault().post(new Event.MainActivityEvent(4));

            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void goToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            goToMarket.setClassName("com.tencent.android.qqdownloader", "com.tencent.pangu.link.LinkProxyActivity");
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            NToast.shortToastBaseApp("请安装应用宝");
        }
    }


}
