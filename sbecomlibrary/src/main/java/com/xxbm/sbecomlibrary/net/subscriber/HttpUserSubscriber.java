package com.xxbm.sbecomlibrary.net.subscriber;


import android.content.Intent;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

import com.xxbm.sbecomlibrary.base.BaseApplation;
import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.base.BaseResponse;
import com.xxbm.sbecomlibrary.net.http.HttpCallback;
import com.xxbm.sbecomlibrary.net.http.HttpException;
import com.xxbm.sbecomlibrary.net.http.HttpUtils;
import com.xxbm.sbecomlibrary.utils.NLog;
import com.xxbm.sbecomlibrary.utils.NToast;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.xxbm.sbecomlibrary.com.GlobalVariable.THREE;


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
        try {
            if(httpResultResponse.code()==200){
                T result = httpResultResponse.body();

                if(result instanceof  BaseDataResponse){
                    BaseDataResponse baseDataResponse = (BaseDataResponse) result;

                    int code = baseDataResponse.getCode();

                    if(code == 1){
                        if (callback != null) {
                            callback.onSuccess(baseDataResponse.getData());
                            callback.onSuccessAll(result);
                        }
                    }else if(code == -2){

                        NToast.shortToastBaseApp("请重新登录");

                        if(null != callback){
                            if(callback.getContext() != null){
                                Intent intent = new Intent("fm.qian.michael.ui.activity.LoginActivity");
                                intent.putExtra("LOGIN",THREE);
                                callback.getContext().startActivity(intent);
                            }
                        }

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
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
