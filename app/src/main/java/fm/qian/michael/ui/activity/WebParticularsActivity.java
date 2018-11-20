package fm.qian.michael.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxbm.sbecomlibrary.utils.NLog;
import com.trello.rxlifecycle2.android.ActivityEvent;

import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.web.JavaScriptInterface;
import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import com.xxbm.sbecomlibrary.net.http.HttpCallback;
import com.xxbm.sbecomlibrary.net.http.HttpException;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.widget.WebViewUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 吕 on 2018/1/8.
 */

public class WebParticularsActivity extends BaseIntensifyActivity implements TextView.OnEditorActionListener
        ,JavaScriptInterface.JsCallBack {

    public static final String WEBTYPE = "WEBTYPE"; //视频  文章
    public static final String WEBID = "WEBID"; //视频  文章

    private String type;

    @BindView(fm.qian.michael.R.id.web_view)
    WebView webView;
    private WebViewUtil webViewUtil;
    @BindView(fm.qian.michael.R.id.jiaZai_layout)
    LinearLayout jiaZai_layout;
    @BindView(fm.qian.michael.R.id.jiaZai_layout_btn)
    LinearLayout jiaZai_layout_btn;


    private String kind;
    private String url = "https://mp.weixin.qq.com/s/D7IAC6En5sh2ludnL29RPw";
    //private String url = "https://v.qq.com/x/page/q07671x669h.html?ptag=qqbrowser";
    private String id;

    /** 视频全屏参数 */


    @OnClick({fm.qian.michael.R.id.again_btn})
    public  void  onClick(View view){
        switch (view.getId()){
            case fm.qian.michael.R.id.base_left_layout:
                finish();
                break;
            case fm.qian.michael.R.id.again_btn:
                setWebLoad(url);//失败时重新打开
                break;
        }
    }

    @Override
    public int getLayout() {
        return fm.qian.michael.R.layout.activity_web_particulars;
    }
    @Override
    public void initView() {
        super.initView();
        setTitleTv("网页");

        Intent intent = getIntent();

        type = intent.getStringExtra(WEBTYPE);
        id = intent.getStringExtra(WEBID);


        webViewUtil = new WebViewUtil();
        webViewUtil.set(jiaZai_layout_btn,jiaZai_layout,webView,this);

        //setWebLoad(url);

        setView();
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch(actionId){
            case EditorInfo.IME_ACTION_SEND:
                NLog.e(NLog.TAG, "send a email: "  + v.getText());
                if(!isLogin()){
                    NToast.shortToastBaseApp(fm.qian.michael.R.string.please_login);
                    return false;
                }
                break;
        }
        return false;
    }

    //js 调用接口  1 回馈  2 签名
    @Override
    public void JscallBack(String operationTypeData) {




    }
    private void setWebLoad(String url) {
        NLog.e(NLog.TAG, "帖子详情URL--->" + url);
        webView.loadUrl(url);
    }



    private void setView(){
        if(GlobalVariable.TWO.equals(type)){
            setTitleTv("视频");
            baseService.video(id, new HttpCallback<ComAll, BaseDataResponse<ComAll>>() {
                @Override
                public void onError(HttpException e) {
                    NToast.shortToastBaseApp(e.getMsg());
                }

                @Override
                public void onSuccess(ComAll comAll) {

                   // setTitleTv(comAll.getTitle());
                    url = comAll.getUrl();
                    setWebLoad(url);
                }
            },WebParticularsActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
        }else if(GlobalVariable.THREE.equals(type)){
            setTitleTv("文章");
            baseService.article(id, new HttpCallback<ComAll, BaseDataResponse<ComAll>>() {
                @Override
                public void onError(HttpException e) {
                    NToast.shortToastBaseApp(e.getMsg());
                }
                @Override
                public void onSuccess(ComAll comAll) {
                   // setTitleTv(comAll.getTitle());
                    url = comAll.getWxurl();
                    setWebLoad(url);
                }
            },WebParticularsActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
        }

    }

    //调用js方法
    private void evaluateJavascript(String jsf, String value){
        String jsCode = "javascript:" + jsf + "('"+ value + "')";

        NLog.e(NLog.TAG, "帖子详情URL--->" + jsCode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(jsCode, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                   // NLog.e(NLog.TAG, "evaluateJavascript--->");
                }
            });
        }else {
            webView.loadUrl(jsCode);
        }
    }
    //保存图片

    //上传图片

    //feedbackFlg:反馈传1 反馈时传1，签名时不传


    //案例的签名与反馈

    //处理视频全拼播放问题


    //屏幕方向发生改变的回调方法
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                /** 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面 */
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
//        if(null != webView){
//            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
//                webView.goBack(); //goBack()表示返回WebView的上一页面
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

       // NLog.e(NLog.TAGOther, "onDestroy0--->");

        webViewUtil.dstroy();


    }


}
