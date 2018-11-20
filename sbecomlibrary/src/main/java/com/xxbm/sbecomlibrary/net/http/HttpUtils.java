package com.xxbm.sbecomlibrary.net.http;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxbm.sbecomlibrary.utils.CheckUtil;

import java.io.File;
import java.io.IOException;


import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;


public class HttpUtils {


    //添加线程管理并订阅
    @SuppressWarnings("unchecked")
    public static void toSubscribe(Observable o,
                                   Observer s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
    //添加线程管理并订阅
    @SuppressWarnings("unchecked")
    public static void toSubscribe(Observable o,
                                   Observer s,
                                   ObservableTransformer transformer){

        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(transformer)
                .subscribe(s);

//        if(NetStateUtils.isNetworkConnected(BaseApplation.getBaseApp())){
//            o.subscribeOn(Schedulers.io())
//                    .unsubscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .compose(transformer)
//                    .subscribe(s);
//        }else {
//            NToast.shortToastBaseApp(BaseApplation.getBaseApp().getString(R.string.无网络));
//        }


    }
    //添加线程管理并订阅
    public static RequestBody buildRequestBody(Object data){
        String jsonstring = getStringValue(data);
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonstring);
    }
    public static RequestBody buildImageRequestBody(File file){
        return   RequestBody.create(okhttp3.MediaType.parse("image/png"), file);
    }

   // private static ObjectMapper objectMapper = null;
    private static ObjectMapper objectMapper =
           new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
                   JsonAutoDetect.Visibility.ANY);

    public static String getStringValue(Object obj) {
        if(null == objectMapper){
            objectMapper =
                    new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
                            JsonAutoDetect.Visibility.ANY);
        }

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        if(obj==null)
            return null;

        try {
            return  objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static  <T> T jsonToBeanT(String s,TypeReference<T> type){
        if(CheckUtil.isEmpty(s))
            return null;
        if(null == objectMapper){
            objectMapper =
                    new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
                            JsonAutoDetect.Visibility.ANY);
        }
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            return objectMapper.readValue(s,type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T jsonToBean(String json, Class<T> cls)  {

        if(CheckUtil.isEmpty(json))
            return null;

        if(null == objectMapper){
            objectMapper =
                    new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
                            JsonAutoDetect.Visibility.ANY);
        }
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            return objectMapper.readValue(json,cls);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



}
