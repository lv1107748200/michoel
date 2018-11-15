package com.xxbm.sbecomlibrary.net.http;




import com.xxbm.sbecomlibrary.com.GlobalVariable;
import com.xxbm.sbecomlibrary.net.Service.AppService;
import com.xxbm.sbecomlibrary.net.Service.ExampleService;
import com.xxbm.sbecomlibrary.net.Service.UserService;
import com.xxbm.sbecomlibrary.net.Service.WXUserService;
import com.xxbm.sbecomlibrary.net.base.BaseService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by 吕 on 2017/10/26.
 */
@Module
public class HttpModule {
    public static final String APP_CODE = "AppCode";
    public static final String APP_CODE_VALUE = "android";
    public static final String DEVID = "devid";
    public static final String OS = "os";
    public static final String VER = "ver";
    public static final String P = "p";
    public static final String Q = "q";
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String SESSIONKEY = "sessionkey";
    @Provides
    @Singleton
    public ExampleService exampleService(){
        HttpServiceSetting httpServiceSetting = new HttpServiceSetting(GlobalVariable.BaseUrlONE);
        httpServiceSetting.builderCallback = new OkHttpRequestBuilderCallback() {
            @Override
            public void builder(Request.Builder builder) {
                builder.addHeader(APP_CODE, APP_CODE_VALUE);    //设置请求头
            }
        };
        httpServiceSetting.logLevel = HttpLoggingInterceptor.Level.BODY;
        Retrofit retrofit = OkHttpClientUtils.buildRetrofit(httpServiceSetting);
        return  retrofit.create(ExampleService.class);
    }

    @Provides
    @Singleton
    public AppService appService(){
        HttpServiceSetting httpServiceSetting = new HttpServiceSetting(GlobalVariable.BaseUrlONE);
        httpServiceSetting.builderCallback = new OkHttpRequestBuilderCallback() {
            @Override
            public void builder(Request.Builder builder) {
                builder.addHeader(APP_CODE, APP_CODE_VALUE);    //设置请求头
            }
        };
        httpServiceSetting.logLevel = HttpLoggingInterceptor.Level.BODY;
        Retrofit retrofit = OkHttpClientUtils.buildRetrofit(httpServiceSetting);
        return  retrofit.create(AppService.class);
    }
    @Provides
    @Singleton
    public UserService userService(){
        HttpServiceSetting httpServiceSetting = new HttpServiceSetting(GlobalVariable.BaseUrlONE);
        httpServiceSetting.builderCallback = new OkHttpRequestBuilderCallback() {
            @Override
            public void builder(Request.Builder builder) {
                builder.addHeader(APP_CODE, APP_CODE_VALUE);    //设置请求头
            }
        };
        httpServiceSetting.logLevel = HttpLoggingInterceptor.Level.BODY;
        Retrofit retrofit = OkHttpClientUtils.buildRetrofit(httpServiceSetting);
        return  retrofit.create(UserService.class);
    }
    @Provides
    @Singleton
    public WXUserService wxUserService(){
        HttpServiceSetting httpServiceSetting = new HttpServiceSetting(GlobalVariable.BaseUrlWX);
        httpServiceSetting.builderCallback = new OkHttpRequestBuilderCallback() {
            @Override
            public void builder(Request.Builder builder) {
                builder.addHeader(APP_CODE, APP_CODE_VALUE);    //设置请求头
            }
        };
        httpServiceSetting.logLevel = HttpLoggingInterceptor.Level.BODY;
        Retrofit retrofit = OkHttpClientUtils.buildRetrofit(httpServiceSetting);
        return  retrofit.create(WXUserService.class);
    }

    //
    @Provides
    @Singleton
    public BaseService baseService(){
        return new BaseService();
    }
}
