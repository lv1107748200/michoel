package com.xxbm.sbecomlibrary.net.Service;


import java.util.List;

import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.base.BaseResponse;
import com.xxbm.sbecomlibrary.net.entry.response.Album;
import com.xxbm.sbecomlibrary.net.entry.response.Base;
import com.xxbm.sbecomlibrary.net.entry.response.Category;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import com.xxbm.sbecomlibrary.net.entry.response.Index;
import com.xxbm.sbecomlibrary.net.entry.response.RankMore;
import com.xxbm.sbecomlibrary.net.entry.response.Ver;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.xxbm.sbecomlibrary.net.http.HttpModule.DEVID;
import static com.xxbm.sbecomlibrary.net.http.HttpModule.ID;
import static com.xxbm.sbecomlibrary.net.http.HttpModule.OS;
import static com.xxbm.sbecomlibrary.net.http.HttpModule.Q;
import static com.xxbm.sbecomlibrary.net.http.HttpModule.P;
import static com.xxbm.sbecomlibrary.net.http.HttpModule.SESSIONKEY;
import static com.xxbm.sbecomlibrary.net.http.HttpModule.USERNAME;
import static com.xxbm.sbecomlibrary.net.http.HttpModule.VER;

/*
 * lv   2018/9/17
 */
public interface AppService {
    //1.版本信息
    @GET("app/ver.ashx")
    Observable<Response<BaseDataResponse<Ver>>> ver(@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //2.首页
    @GET("app/index.ashx")
    Observable<Response<BaseDataResponse<Index>>> index(@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //3.全部排行榜
    @GET("app/ranklist.ashx")
    Observable<Response<BaseDataResponse<List<ComAll>>>> ranklist(@Query("tid") String tid
            , @Query("day") String day,@Query(P) String p,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //4.首页更多推荐 p 页码
    @GET("app/recommend.ashx")
    Observable<Response<BaseDataResponse<List<Base>>>> recommend(@Query(P) String p,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //5.专题
    @GET("app/topic.ashx")
    Observable<Response<BaseResponse<ComAll,List<Base>>>> topic(@Query(ID) String id,@Query(P) String p,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //6.专辑
    @GET("app/album.ashx")
    Observable<Response<BaseResponse<Album,List<ComAll>>>> album(@Query(ID) String id
                                                 , @Query(P) String p
                                                 , @Query(USERNAME) String phone
                                                 , @Query(SESSIONKEY) String sessionkey,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);

    //7. 音频
    @GET("app/audio.ashx")
    Observable<Response<BaseDataResponse<ComAll>>> audio(@Query(ID) String id
            , @Query("phone") String phone
            , @Query(SESSIONKEY) String sessionkey,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //8. 播放次数上报
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("app/pta.ashx")
    Observable<Response<BaseDataResponse>> pta(@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //9. 视频
    @GET("app/video.ashx")
    Observable<Response<BaseDataResponse<ComAll>>> video(@Query(ID) String id,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //10. 图文
    @GET("app/article.ashx")
    Observable<Response<BaseDataResponse<ComAll>>> article(@Query(ID) String id,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //11. 文章搜索
    @GET("app/articlelist.ashx")
    Observable<Response<BaseDataResponse<List<Base>>>> articlelist(@Query(Q) String q,
                                                                   @Query(P) String p,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //12. 搜索热门关键词
    @GET("app/search_hotwords.ashx")
    Observable<Response<BaseDataResponse<List<Base>>>> search_hotwords(@Query("tid") String tid,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //13. 分类
    @GET("app/category.ashx")
    Observable<Response<BaseDataResponse<List<Category>>>> category(@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
    //14. 自选
    @GET("app/search.ashx")
    Observable<Response<BaseDataResponse<List<ComAll>>>> search(@Query("t") String t,
                                                                @Query(Q) String q,
                                                                @Query(P) String p,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);

    //15. ISBN
    @GET("app/isbn.ashx")
    Observable<Response<BaseDataResponse<ComAll>>> isbn(@Query("isbn") String isbn,@Query(DEVID) String devid,@Query(OS) String os,@Query(VER) String ver);
}
