package fm.qian.michael.ui.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bigkoo.pickerview.builder.TimePickerBuilder;

import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.xxbm.sbecomlibrary.utils.DisplayUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.entry.response.UserInfo;
import com.xxbm.sbecomlibrary.net.http.HttpCallback;
import com.xxbm.sbecomlibrary.net.http.HttpException;
import fm.qian.michael.utils.DateUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.widget.RoundedImage.RoundedImageView;
import fm.qian.michael.widget.dialog.LoadingDialog;
import fm.qian.michael.widget.pop.CustomPopuWindConfig;
import fm.qian.michael.widget.pop.PopPlayListWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.widget.pop.PopSetNameWindow;
import fm.qian.michael.widget.single.UserInfoManger;


/*
 * lv   2018/9/8
 */
public class UserAcrtivity extends BaseIntensifyActivity {

    public static final String USERA = "USERA";
    public static final String GET = "get";
    public static final String SET = "set";

    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;

    private String sex;

    private int type;

    private List<LocalMedia> selectList = new ArrayList<>();
    private int maxSelectNum = 1;


    private TimePickerView timePickerViewRight;
    private PopSetNameWindow popSetNameWindow;

    private LoadingDialog loadingDialog;


    @BindView(R.id.head_portrait )
    RoundedImageView headPortrait;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.nc_tv)
    TextView nc_tv;
    @BindView(R.id.xb_tv)
    TextView xb_tv;//性别
    @BindView(R.id.sr_tv)
    TextView sr_tv;//生日
    @BindView(R.id.layout_sr)
    LinearLayout layoutSr;

    @OnClick({R.id.user_layout,
            R.id.wodexinxi_layout, R.id.baobaoxinxi_layout, R.id.sczj_layout})
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.base_left_layout:
                finish();
                break;
            case R.id.user_layout:
                selP();
                break;
            case R.id.wodexinxi_layout:
                if(!isLogin()){
                    return;
                }
                setPopSetNameWindow(1);

                break;
            case R.id.baobaoxinxi_layout:

                if(!isLogin()){
                    return;
                }
                setPopSetNameWindow(2);

                break;
            case R.id.sczj_layout://生日
                if(!isLogin()){
                    return;
                }
                timePickerViewRight.show();

                break;
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_user;
    }

    @Override
    public void initView() {
        super.initView();

        loadingDialog = new LoadingDialog(this);

        Intent intent = getIntent();

        type = intent.getIntExtra(USERA,-1);

        switch (type){
            case ONE:
                setTitleTv(getString(R.string.我的信息));
                layoutSr.setVisibility(View.GONE);
                break;
            case TWO:
                setTitleTv(getString(R.string.宝宝信息));

                break;
            case THREE:
                setTitleTv(getString(R.string.收藏的专辑));

                break;
            case FOUR:
                setTitleTv(getString(R.string.已购买专辑));

                break;
        }

        setTimePickerViewRight();

        getUserMessage();//初始化
    }

    private void getUserMessage(){
        if(isLogin()){
            UserInfo userInfo = new UserInfo();

            userInfo.setAct(GET);
            userInfo.setUsername(UserInfoManger.getInstance().getUserName());
            userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());

            user_infoGet(userInfo);
        }
    }

    private void user_infoGet(UserInfo userInfo){
        baseService.user_info(userInfo, new HttpCallback<UserInfo, BaseDataResponse<UserInfo>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(UserInfo userInfo) {
               // NToast.shortToastBaseApp("成功");

                if(type == ONE){
                    EventBus.getDefault().post(new Event.LoginEvent(GlobalVariable.ZERO));

                    GlideUtil.setGlideImage(UserAcrtivity.this
                            ,userInfo.getLogo()
                            ,headPortrait,R.drawable.myicon);

                    nickname.setText(userInfo.getNickname());
                    nc_tv.setText(userInfo.getNickname());

                    sex = userInfo.getSex();
                    xb_tv.setText(sex);
                }else if(type == TWO){

                    GlideUtil.setGlideImage(UserAcrtivity.this
                            , userInfo.getBabylogo()
                            ,headPortrait,R.drawable.myicon);

                    nickname.setText(userInfo.getBabynickname());

                    if(CheckUtil.isEmpty(userInfo.getBabynickname())){
                        nc_tv.setText("未设置");
                    }else {
                        nc_tv.setText(userInfo.getBabynickname());
                    }
                    sex = userInfo.getBabysex();
                    xb_tv.setText(sex);

                    sr_tv.setText(userInfo.getBabybirthday());
                }

            }
        }.setContext(this),UserAcrtivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }



    private void user_infoSet(UserInfo userInfo){
        baseService.user_info(userInfo, new HttpCallback<UserInfo, BaseDataResponse<UserInfo>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(UserInfo userInfo) {
               // NToast.shortToastBaseApp("成功");


                if(null != popSetNameWindow){
                    popSetNameWindow.dismiss();
                }

                UserInfoManger.getInstance().clear();

                getUserMessage();//设置之后

            }
        }.setContext(this),UserAcrtivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    private void user_logo(UserInfo userInfo,String s){

        loadingDialog.show("正在上传");

        baseService.user_logo(userInfo,new File(s), new HttpCallback<Object, BaseDataResponse>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
                loadingDialog.diss();
            }

            @Override
            public void onSuccess(Object userInfo) {
               // NToast.shortToastBaseApp("成功");
                loadingDialog.diss();
                getUserMessage();

            }
        }.setContext(this),UserAcrtivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    //初始化 姓名 性别 设置
    private void setPopSetNameWindow(int i){

        if(null == popSetNameWindow){
            popSetNameWindow = new PopSetNameWindow(this,new CustomPopuWindConfig.Builder(this)
                    .setOutSideTouchable(true)
                    .setFocusable(true)
                    .setAnimation(R.style.popup_hint_anim)
                    .setWith((DisplayUtils.getScreenWidth(this) - DisplayUtils.dip2px(this,80)))
                    .build());
            popSetNameWindow.setSetNameCallBack(new PopSetNameWindow.SetNameCallBack() {
                @Override
                public void callBackName(String s) {
                    if(CheckUtil.isEmpty(s)){
                        NToast.shortToastBaseApp("请输入昵称");
                    }else {

                        UserInfo userInfo = new UserInfo();

                        userInfo.setAct(SET);
                        userInfo.setUsername(UserInfoManger.getInstance().getUserName());
                        userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());

                        if(type == ONE){
                            userInfo.setNickname(s);
                        }else if(type == TWO){
                            userInfo.setBabynickname(s);
                        }

                        user_infoSet(userInfo);//昵称
                    }
                }

                @Override
                public void callBackSex(String s) {
                    UserInfo userInfo = new UserInfo();

                    userInfo.setAct(SET);
                    userInfo.setUsername(UserInfoManger.getInstance().getUserName());
                    userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());

                    if(type == ONE){
                        userInfo.setSex(s);
                    }else if(type == TWO){
                        userInfo.setBabysex(s);
                    }

                    user_infoSet(userInfo);//性别
                }
            });
            popSetNameWindow.setShow(i,sex);
            popSetNameWindow.show(nickname);
        }else {
            popSetNameWindow.setShow(i,sex);
            popSetNameWindow.show(nickname);
        }

    }

    //初始化时间选择器
    private void setTimePickerViewRight() {
         timePickerViewRight = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
//                DateUtils.dateToString(date, "yyyy-MM-dd");
                NLog.e(NLog.TAGOther,"时间 --->"+ DateUtils.dateToString(date, "yyyy-MM-dd"));

                String dasss = DateUtils.dateToString(date, "yyyy-MM-dd");

                UserInfo userInfo = new UserInfo();
                userInfo.setAct(SET);
                userInfo.setUsername(UserInfoManger.getInstance().getUserName());
                userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());
                userInfo.setBabybirthday(dasss);

                user_infoSet(userInfo);//生日

            }
        })
          .setSubmitColor(ContextCompat.getColor(this,R.color.color_F86))//确定按钮文字颜
          .setCancelColor(ContextCompat.getColor(this,R.color.color_292))//取消按钮文字颜色
          .build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        NLog.e(NLog.TAGOther,"图片-----》"+ media.getPath());
                    }

                    if(!CheckUtil.isEmpty(selectList)){
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUsername(UserInfoManger.getInstance().getUserName());
                        userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());

                        if(type == ONE){
                            userInfo.setAct("my");

                        }else if(type == TWO){
                            userInfo.setAct("baby");
                        }

                        user_logo(userInfo,selectList.get(0).getPath());
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        PictureFileUtils.deleteCacheDirFile(this);
        super.onDestroy();
    }

    private void selP(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(fm.qian.michael.R.style.picture_white_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
//                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(false)// 是否可预览视频
                .enablePreviewAudio(false) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .enableCrop(false)// 是否裁剪
                .compress(false)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(true)// 是否圆形裁剪
//                .showCropFrame(cb_showCropFrame.isChecked())// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
//                .showCropGrid(cb_showCropGrid.isChecked())// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
//                .openClickSound(cb_voice.isChecked())// 是否开启点击声音
                .selectionMedia(selectList)// 是否传入已选图片
                .isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(true) // 裁剪是否可旋转图片
                //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.videoSecond()//显示多少秒以内的视频or音频也可适用
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }
}
