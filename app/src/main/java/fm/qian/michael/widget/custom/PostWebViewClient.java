package fm.qian.michael.widget.custom;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by 吕 on 2017/10/31.
 */

public class PostWebViewClient extends WebViewClient {
    private boolean needClear = false;
    private WebView m_integral_web;

    public void setM_integral_web(WebView m_integral_web) {
        this.m_integral_web = m_integral_web;
    }

    public void setNeedClear(boolean needClear) {
        this.needClear = needClear;
    }


    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (needClear) {
            needClear = false;
            m_integral_web.clearHistory();//清除历史记录
        }
        super.doUpdateVisitedHistory(view, url, isReload);
    }
}
