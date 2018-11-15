package fm.qian.michael.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hr.bclibrary.utils.CheckUtil;
import com.hr.bclibrary.utils.NLog;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.web.JavaScriptInterface;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.WebViewUtil;
import fm.qian.michael.widget.pop.CustomPopuWindConfig;
import fm.qian.michael.widget.pop.PopShareWindow;
import fm.qian.michael.widget.web.X5WebView;

/**
 * Created by 吕 on 2018/1/8.
 */

public class WebTBSParticularsActivity extends BaseIntensifyActivity {

    public static final String WEBTYPE = "WEBTYPE"; //视频  文章
    public static final String WEBID = "WEBID"; //视频  文章

    private String type;
    private X5WebView mWebView; //

    @BindView(R.id.webView1)
    FrameLayout frameLayout;
    @BindView(R.id.progressBar)
    ProgressBar jiaZai_layout;
    @BindView(R.id.jiaZai_layout_btn)
    LinearLayout jiaZai_layout_btn;
    @BindView(R.id.base_right_layout_two)
    LinearLayout base_right_layout_two;
    private String kind;
    private String url = "https://www.qian.fm/agreement.htm";
    //private String url = "https://v.qq.com/x/page/q07671x669h.html?ptag=qqbrowser";
    private String id;
    private PopShareWindow popShareWindow;
    private ComAll comAll;
    @OnClick({ R.id.again_btn,R.id.base_right_layout_two})
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.again_btn:
                setWebLoad(url);//失败时重新打开
                break;
            case R.id.base_right_layout_two:
                if(null == comAll)
                    return;

                String share = comAll.getWxurl();

                if(CheckUtil.isEmpty(share)) {
                 share = comAll.getUrl();
                }

                if(null == popShareWindow){
                    popShareWindow = new PopShareWindow(this,new CustomPopuWindConfig.Builder(this)
                            .setOutSideTouchable(true)
                            .setFocusable(true)
                            .setTouMing(false)
                            .setAnimation(R.style.popup_hint_anim)
                            .setWith((com.hr.bclibrary.utils.DisplayUtils.getScreenWidth(this)))
                            .build());

                    popShareWindow.setShareData(new PopShareWindow.ShareData(comAll.getTitle(),comAll.getCover(),comAll.getBrief(),share));

                    popShareWindow.show(view);
                }else {
                    popShareWindow.setShareData(new PopShareWindow.ShareData(comAll.getTitle(),comAll.getCover(),comAll.getBrief(),share));

                    popShareWindow.show(view);
                }


                break;
        }
    }

    @Override
    public boolean isAddGifImage() {
        return true;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_web_tbs_particulars;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(null != intent)
        initView(intent);
    }
    @Override
    public void initView(Intent intent) {
        super.initView();
        setTitleTv("网页");



        type = intent.getStringExtra(WEBTYPE);
        id = intent.getStringExtra(WEBID);

        setmWebView();//初始化
        setView();
    }

    private void setmWebView(){

        mWebView = new X5WebView(this, null);

        frameLayout.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(com.tencent.smtt.sdk.WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                jiaZai_layout.setVisibility(View.VISIBLE);
                jiaZai_layout_btn.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(com.tencent.smtt.sdk.WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
                jiaZai_layout.setVisibility(View.GONE);
                jiaZai_layout_btn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
                super.onPageFinished(view, url);
                jiaZai_layout.setVisibility(View.GONE);
            }
        });


        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(com.tencent.smtt.sdk.WebView webView, int i) {
                super.onProgressChanged(webView, i);
                jiaZai_layout.setProgress(i);
            }

            @Override
            public boolean onJsConfirm(com.tencent.smtt.sdk.WebView arg0, String arg1, String arg2,
                                       JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;

            // /////////////////////////////////////////////////////////
            //
            /**
             * 全屏播放配置
             */
            @Override
            public void onShowCustomView(View view,
                                         IX5WebChromeClient.CustomViewCallback customViewCallback) {
                FrameLayout normalView = (FrameLayout) findViewById(R.id.web_filechooser);
                ViewGroup viewGroup = (ViewGroup) normalView.getParent();
                viewGroup.removeView(normalView);
                viewGroup.addView(view);
                myVideoView = view;
                myNormalView = normalView;
                callback = customViewCallback;
            }

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onJsAlert(com.tencent.smtt.sdk.WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */
                return super.onJsAlert(null, arg1, arg2, arg3);
            }
        });


        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);

        long time = System.currentTimeMillis();
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

    }


    private void setView(){
        if(GlobalVariable.TWO.equals(type)){
            base_right_layout_two.setVisibility(View.VISIBLE);
            base_right_layout.setVisibility(View.GONE);
            setTitleTv("视频");
            baseService.video(id, new HttpCallback<ComAll, BaseDataResponse<ComAll>>() {
                @Override
                public void onError(HttpException e) {
                    NToast.shortToastBaseApp(e.getMsg());
                }

                @Override
                public void onSuccess(ComAll comAll) {
                    WebTBSParticularsActivity.this.comAll = comAll;
                   // setTitleTv(comAll.getTitle());
                    url = comAll.getUrl();
                    setWebLoad(url);
                }
            },WebTBSParticularsActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
        }else if(GlobalVariable.THREE.equals(type)){
            base_right_layout_two.setVisibility(View.VISIBLE);
            base_right_layout.setVisibility(View.GONE);
            setTitleTv("文章");
            baseService.article(id, new HttpCallback<ComAll, BaseDataResponse<ComAll>>() {
                @Override
                public void onError(HttpException e) {
                    NToast.shortToastBaseApp(e.getMsg());
                }
                @Override
                public void onSuccess(ComAll comAll) {
                   // setTitleTv(comAll.getTitle());
                    WebTBSParticularsActivity.this.comAll = comAll;
                    url = comAll.getWxurl();
                    setWebLoad(url);
                }
            },WebTBSParticularsActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
        }else if(GlobalVariable.FOUR.equals(type)){
            setTitleTv("服务协议");
            url = "https://www.qian.fm/agreement.htm";
            setWebLoad(url);
        }else if(GlobalVariable.FIVE.equals(type)){
            setTitleTv("关于我们");
            url = "https://www.qian.fm/about.htm";
            setWebLoad(url);
        }else if(GlobalVariable.SIX.equals(type)){
            setTitleTv("联系我们");
            url = "https://www.qian.fm/contact.htm";
            setWebLoad(url);
        }

    }
    private void setWebLoad(String url) {
        NLog.e(NLog.TAG, "帖子详情URL--->" + url);
        mWebView.loadUrl(url);
    }
    //屏幕方向发生改变的回调方法
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                if(GlobalVariable.TWO.equals(type)){//视频的话直接关闭
                    finish();
                }else{
                    mWebView.goBack();
                }

                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onDestroy() {

        if (mWebView != null){
            mWebView.clearHistory();
            mWebView.destroy();
        }

        super.onDestroy();


    }


}
