package fm.qian.michael.net.subscriber;


import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.base.BaseResponse;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.net.http.HttpUtils;
import fm.qian.michael.utils.NLog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 */
public class HttpUserSubscriber<T> implements Observer<Response<T>> {
    HttpCallback callback;
    TypeReference aClass;

    public HttpUserSubscriber(HttpCallback callback) {
        super();
        this.callback = callback;
    }
    public HttpUserSubscriber(HttpCallback callback,TypeReference  cls) {
        super();
        this.callback = callback;
        this.aClass = cls;
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
                BaseDataResponse baseDataResponse = (BaseDataResponse) result;


                if(baseDataResponse.getCode() == 1){
                    if (callback != null) {
                        callback.onSuccess(baseDataResponse.getData());
                        callback.onSuccessAll(result);
                    }
                }else if(baseDataResponse.getCode() == -2){

                }else {
                    if (callback != null) {
                        callback.onError(new HttpException(((BaseDataResponse) result).getCode(),
                                ((BaseDataResponse) result).getMsg()));
                    }
                }
            }else if(result instanceof ResponseBody){

                ResponseBody responseBody = (ResponseBody) result;

                try {


                    String resjson = responseBody.string();
                    NLog.e(NLog.TAG,resjson);


                    if(null != aClass){
                        if(null != callback){
                            callback.onSuccess( HttpUtils.jsonToBeanT(resjson,aClass));
                        }
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if(result instanceof  BaseResponse) {
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
