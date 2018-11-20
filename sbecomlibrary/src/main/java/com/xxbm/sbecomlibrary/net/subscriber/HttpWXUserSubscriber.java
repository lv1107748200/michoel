package com.xxbm.sbecomlibrary.net.subscriber;


import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.base.BaseResponse;
import com.xxbm.sbecomlibrary.net.entry.response.Base;
import com.xxbm.sbecomlibrary.net.http.HttpCallback;
import com.xxbm.sbecomlibrary.net.http.HttpException;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;


/**
 */
public class HttpWXUserSubscriber<T> implements Observer<Response<T>> {
    HttpCallback callback;

    public HttpWXUserSubscriber(HttpCallback callback) {
        super();
        this.callback = callback;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
      //  NToast.shortToastBaseApp("访问失败!");
        if (callback != null) {
            callback.onError(new HttpException(-100,e.getMessage()));
        }
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onNext(Response<T> httpResultResponse) {
        if(httpResultResponse.code()==200){
            T result = httpResultResponse.body();

            if(null != callback){
                callback.onSuccess(result);
            }

        } else {
            if (callback != null) {
                callback.onError(new HttpException(httpResultResponse.code(),httpResultResponse.message()));
            }
        }
    }


}
