package fm.qian.michael.net.Service;


import java.util.List;
import java.util.Map;

import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.Category;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.UserInfo;
import fm.qian.michael.net.entry.response.YZMOrSID;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/*
 * lv   2018/9/27 用户相关
 */
public interface UserService {

    //登陆
    @FormUrlEncoded
    @POST("app/user_login.ashx")
    Observable<Response<BaseDataResponse<UserInfo>>> user_login(@FieldMap Map<String,String> map);

    //注册
    @FormUrlEncoded
    @POST("app/user_reg.ashx")
    Observable<Response<BaseDataResponse<UserInfo>>> user_reg(@FieldMap Map<String,String> map);

    //获取验证码时  首先要获取 sid
    @GET("getyzm.ashx")
    Observable<Response<BaseDataResponse<YZMOrSID>>> getyzm(@Query("sid") String sid,@Query("random") String random);
    //短信验证码
    @FormUrlEncoded
    @POST("app/user_sms.ashx")
    Observable<Response<BaseDataResponse>> user_sms(@FieldMap Map<String,String> map);

    //用户绑定微信
    @FormUrlEncoded
    @POST("app/user_bind.ashx")
    Observable<Response<BaseDataResponse<UserInfo>>> user_bind(@FieldMap Map<String,String> map);

    //同步购买记录
    @GET("app/user_tbxiaoe.ashx")
    Observable<Response<BaseDataResponse>> user_tbxiaoe(@Query("sessionkey") String sessionkey,
                                                        @Query("username") String username);

    //我的信息 宝宝信息
    @FormUrlEncoded
    @POST("app/user_info.ashx")
    Observable<Response<BaseDataResponse<UserInfo>>> user_info(@FieldMap Map<String,String> map);

    //上传头像
    @Multipart
    @POST("app/user_logo.ashx")
    Observable<Response<BaseDataResponse<Object>>> user_logo(@Part MultipartBody.Part file,@PartMap Map<String, RequestBody> params);

    //收藏的专辑
    @FormUrlEncoded
    @POST("app/user_favorite.ashx")
    Observable<Response<BaseDataResponse>> user_favorite(@FieldMap Map<String,String> map);
    //收藏的专辑 list
    @FormUrlEncoded
    @POST("app/user_favorite.ashx")
    Observable<Response<BaseDataResponse<List<ComAll>>>> user_favorite_list(@FieldMap Map<String,String> map);

    //已购买的专辑
    @FormUrlEncoded
    @POST("app/user_album.ashx")
    Observable<Response<BaseDataResponse<List<ComAll>>>> user_album(@FieldMap Map<String,String> map);

    //我的播单
    @FormUrlEncoded
    @POST("app/user_broadcast.ashx")
    Observable<Response<BaseDataResponse>> user_broadcast(@FieldMap Map<String,String> map);
    //我的播单 分页
    @FormUrlEncoded
    @POST("app/user_broadcast.ashx")
    Observable<Response<BaseDataResponse<List<ComAll>>>> user_broadcastall(@FieldMap Map<String,String> map);


    //播单详情列表
    @FormUrlEncoded
    @POST("app/user_broadcastlist.ashx")
    Observable<Response<BaseDataResponse<ComAll>>> user_broadcastlist(@FieldMap Map<String,String> map);

    //播单详情列表 list
    @FormUrlEncoded
    @POST("app/user_broadcastlist.ashx")
    Observable<Response<BaseDataResponse<List<ComAll>>>> user_broadcastlistAll(@FieldMap Map<String,String> map);
}
