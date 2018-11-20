package com.xxbm.sbecomlibrary.net.Service;


import java.util.List;

import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import com.xxbm.sbecomlibrary.net.entry.response.WXAccessData;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
 * lv   2018/9/27
 */
public interface WXUserService {
    //
    @GET("sns/oauth2/access_token")
    Observable<Response<WXAccessData>> access_token(@Query("appid") String appid
            , @Query("secret") String secret, @Query("code") String code, @Query("grant_type") String grant_type);
    //
    @GET("sns/userinfo")
    Observable<Response<WXAccessData>> userinfo(@Query("access_token") String access_token
            , @Query("openid") String openid);


}
