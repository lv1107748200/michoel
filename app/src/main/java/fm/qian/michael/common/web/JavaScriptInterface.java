package fm.qian.michael.common.web;

import android.webkit.JavascriptInterface;

/**
 * Created by 吕 on 2018/1/17.
 * webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");
 */

public class JavaScriptInterface extends Object{

    private JsCallBack jsCallBack;

    public JavaScriptInterface(JsCallBack jsCallBack) {
        this.jsCallBack = jsCallBack;
    }

    // 1 回馈  2 签名
    @JavascriptInterface
    public void setfFeedback(String operationTypeData) {
        if(null != jsCallBack){
            jsCallBack.JscallBack(operationTypeData);
        }
    }

    public interface JsCallBack{
        public void JscallBack(String operationTypeData);
    }
}
