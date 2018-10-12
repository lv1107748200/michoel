package fm.qian.michael.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import fm.qian.michael.widget.custom.PostWebViewClient;


/**
 * Created by 吕 on 2017/9/30.
 */

public class WebViewUtil {

    private static boolean DEBUG = true;
    public PostWebViewClient postWebViewClient;

    public WebView m_integral_web;

    public   void set(final View btn, final View context, WebView m_integral_web, final Activity activity){

        this.m_integral_web =  m_integral_web;

        postWebViewClient  = new PostWebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (DEBUG) {
                    System.out.println("跳转后的URL--->"+url);
                }
                return true;
            }
        };
        m_integral_web.setWebViewClient(postWebViewClient);
        //声明WebSettings子类
        WebSettings webSettings = m_integral_web.getSettings();

//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

//支持插件
        //  webSettings.setPluginsEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);

//设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

//缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

//其他细节操作
//        LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
//        LOAD_DEFAULT: 根据cache-control决定是否从网络上取数据。
//        LOAD_CACHE_NORMAL: API level 17中已经废弃, 从API level 11开始作用同LOAD_DEFAULT模式
//        LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
//        LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webSettings.setBlockNetworkImage(false);
//// 支持内容重新布局
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //设置WebChromeClient类
        m_integral_web.setWebChromeClient(new WebChromeClient() {


            /*** 视频播放相关的方法 **/

            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(activity);
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {

            }

            @Override
            public void onHideCustomView() {


            }

            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (DEBUG) {
                    System.out.println("标题在这里");
                    Log.e("WEB", title);
                }
            }

            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    if (DEBUG) {
                        String progress = newProgress + "%";
                        Log.e("WEB", progress);
                    }

                   // progressBar1.setVisibility(View.VISIBLE);
                   // progressBar1.setProgress(newProgress);


                } else if (newProgress == 100) {
                    // progressBar1.setProgress(newProgress);
                    //progressBar1.setVisibility(View.GONE);
                }
            }
        });
        //设置WebViewClient类
        m_integral_web.setWebViewClient(new WebViewClient() {
            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                System.out.println("开始加载了");
                Log.e("WEB", "开始加载了");
                Log.e("WEB", url);
                context.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                context.setVisibility(View.GONE);
                btn.setVisibility(View.VISIBLE);

            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("WEB", "结束");
                Log.e("WEB", url);
                context.setVisibility(View.GONE);
            }
        });
    }

    public void dstroy(){
        if (m_integral_web != null) {
           // NLog.e(NLog.TAGOther, "onDestroy1--->");
            m_integral_web.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            m_integral_web.clearHistory();
            ((ViewGroup) m_integral_web.getParent()).removeView(m_integral_web);
            m_integral_web.destroy();
            m_integral_web = null;
        }
    }

    @SuppressLint("JavascriptInterface")
    public void initJs(WebView webView, Object o, String name){

        webView.addJavascriptInterface(o,name);

    }
}
