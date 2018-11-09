package fm.qian.michael.net.http;


import android.content.Context;

import fm.qian.michael.R;
import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.utils.NToast;

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
        NToast.shortToastBaseApp(BaseApplation.getBaseApp().getString(R.string.无网络));
    }
}
