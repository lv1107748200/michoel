package fm.qian.michael.widget.pop;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.common.GlobalVariable;
import com.xxbm.sbecomlibrary.net.entry.response.Album;
import fm.qian.michael.utils.NotificationUitls;
import fm.qian.michael.wxapi.Constants;

/*
 * lv   2018/10/10
 */
public class PopShareWindow extends BasePopupWindow {

    private ShareData shareData;

    private boolean scene;

    private Context context;

    public PopShareWindow(Context context, CustomPopuWindConfig config) {
        super(config);
        this.context = context;
    }
    @OnClick({R.id.l_layout,R.id.q_layout})
    public  void  OnClick(View view){
        switch (view.getId()){
            case R.id.l_layout:
                scene = true;
                share();
                break;
            case R.id.q_layout:
                scene = false;
                share();
                break;
        }
    }
    @Override
    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_share,
                null,false);
        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void show(View parent) {
        super.show(parent);

        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, Gravity.BOTTOM,0, 0);

        } else {
            this.dismiss();
        }
    }

    @Override
    public void onDiss() {
        if(null !=  Constants.wx_api){
            Constants.wx_api.unregisterApp();
            Constants.wx_api = null;
        }

    }

    public void setShareData(ShareData album) {
        this.shareData = album;
    }


    private void share(){

//        RequestOptions options = new RequestOptions()
//                .error(R.mipmap.logo)
//                .priority(Priority.LOW);
//        Glide.with(context)
//                .asBitmap()
//                .apply(options)
//                .load(shareData.getCover())
//                .into(new SimpleTarget<Bitmap>(){
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//
//                        setData(resource);
//
//                    }
//
//                    @Override
//                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//
//                        setData(((BitmapDrawable) errorDrawable).getBitmap());
//
//                    }
//                });
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.logo);
        setData(bitmap);

    }


    private void setData(Bitmap bitmap){
        WXWebpageObject wxWebpageObject = new WXWebpageObject();

        wxWebpageObject.webpageUrl = shareData.getShareUrl();

        WXMediaMessage mediaMessage = new WXMediaMessage(wxWebpageObject);

        mediaMessage.title = shareData.getTitle();
        mediaMessage.description = shareData.getBrief();

        mediaMessage.thumbData =bmpToByteArray(bitmap,false) ;

        Constants.wx_api = WXAPIFactory.createWXAPI(context, Constants.APP_ID, true);
        Constants.wx_api.registerApp(Constants.APP_ID);

        SendMessageToWX.Req req = new SendMessageToWX.Req();



        req.transaction = "webpage" ;

        req.message = mediaMessage;

        req.scene = scene?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;


        Constants.type = GlobalVariable.TWO;//分享
        Constants.wx_api.sendReq(req);
    }


    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 10,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }


    public static class ShareData{
        private String title;
        private String cover;
        private String brief;
        private String shareUrl;

        public ShareData(String title, String cover, String brief, String shareUrl) {
            this.title = title;
            this.cover = cover;
            this.brief = brief;
            this.shareUrl = shareUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }
    }
}
