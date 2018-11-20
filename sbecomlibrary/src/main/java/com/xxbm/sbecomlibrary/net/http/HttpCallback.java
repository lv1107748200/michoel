package com.xxbm.sbecomlibrary.net.http;


import android.content.Context;

import com.xxbm.sbecomlibrary.R;
import com.xxbm.sbecomlibrary.base.BaseApplation;
import com.xxbm.sbecomlibrary.utils.NToast;


public abstract class HttpCallback<T,A> extends CallBack<T,A>{

    public Context context;

    public abstract  void onError(HttpException e);

    public HttpCallback<T,A> setContext(Context context) {
        this.context = context;
        return this;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onSuccess(T t) {

    }

    @Override
    public void onSuccessAll(A k) {

    }

    @Override
    public void onNotNet() {
       NToast.shortToastBaseApp("无网络连接，请连接至网络");
    }
}
