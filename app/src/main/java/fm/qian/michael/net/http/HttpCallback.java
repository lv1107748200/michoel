package fm.qian.michael.net.http;


import android.content.Context;

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
}
