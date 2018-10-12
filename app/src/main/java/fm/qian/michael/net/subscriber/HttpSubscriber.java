package fm.qian.michael.net.subscriber;


import android.content.Intent;


import fm.qian.michael.base.BaseApplation;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.base.BaseResponse;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;


/**
 */
public class HttpSubscriber<T> implements Observer<Response<T>> {
    HttpCallback callback;

    public HttpSubscriber(HttpCallback callback) {
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

            if(result instanceof  BaseDataResponse){
                if(((BaseDataResponse) result).getCode() == 1){
                    if (callback != null) {
                        callback.onSuccess(((BaseDataResponse) result).getData());
                        callback.onSuccessAll(result);
                    }
                } else {
                    if (callback != null) {
                        callback.onError(new HttpException(((BaseDataResponse) result).getCode(),
                                ((BaseDataResponse) result).getMsg()));
                    }
                }
            }else if(result instanceof  BaseResponse) {
                if(((BaseResponse) result).getCode() == 1){
                    if (callback != null) {
                        callback.onSuccessAll(result);
                    }
                } else {
                    if (callback != null) {
                        callback.onError(new HttpException(((BaseResponse) result).getCode(),
                                ((BaseResponse) result).getMsg()));
                    }
                }
            }


        } else {
            if (callback != null) {
                callback.onError(new HttpException(httpResultResponse.code(),httpResultResponse.message()));
            }
        }
    }


}
