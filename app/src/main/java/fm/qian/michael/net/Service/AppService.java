package fm.qian.michael.net.Service;


import java.util.List;

import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.base.BaseResponse;
import fm.qian.michael.net.entry.response.Album;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.Category;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.Index;
import fm.qian.michael.net.entry.response.RankMore;
import fm.qian.michael.net.entry.response.Ver;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/*
 * lv   2018/9/17
 */
public interface AppService {
    //1.版本信息
    @GET("app/ver.ashx")
    Observable<Response<BaseDataResponse<Ver>>> ver();
    //2.首页
    @GET("app/index.ashx")
    Observable<Response<BaseDataResponse<Index>>> index();
    //3.全部排行榜
    @GET("app/ranklist.ashx")
    Observable<Response<BaseDataResponse<List<ComAll>>>> ranklist(@Query("tid") String tid
            , @Query("day") String day,@Query("p") String p);
    //4.首页更多推荐 p 页码
    @GET("app/recommend.ashx")
    Observable<Response<BaseDataResponse<List<Base>>>> recommend(@Query("p") String p);
    //5.专题
    @GET("app/topic.ashx")
    Observable<Response<BaseResponse<ComAll,List<Base>>>> topic(@Query("id") String id,@Query("p") String p);
    //6.专辑
    @GET("app/album.ashx")
    Observable<Response<BaseResponse<Album,List<ComAll>>>> album(@Query("id") String id
                                                 , @Query("p") String p
                                                 , @Query("phone") String phone
                                                 , @Query("sessionkey") String sessionkey);

    //7. 音频
    @GET("app/audio.ashx")
    Observable<Response<BaseDataResponse<ComAll>>> audio(@Query("id") String id
            , @Query("phone") String phone
            , @Query("sessionkey") String sessionkey);
    //8. 播放次数上报
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("app/pta.ashx")
    Observable<Response<BaseDataResponse>> pta();
    //9. 视频
    @GET("app/video.ashx")
    Observable<Response<BaseDataResponse<ComAll>>> video(@Query("id") String id);
    //10. 图文
    @GET("app/article.ashx")
    Observable<Response<BaseDataResponse<ComAll>>> article(@Query("id") String id);
    //11. 文章搜索
    @GET("app/articlelist.ashx")
    Observable<Response<BaseDataResponse<List<Base>>>> articlelist(@Query("q") String q,
                                                                   @Query("p") String p);
    //12. 搜索热门关键词
    @GET("app/search_hotwords.ashx")
    Observable<Response<BaseDataResponse<List<Base>>>> search_hotwords(@Query("tid") String tid);
    //13. 分类
    @GET("app/category.ashx")
    Observable<Response<BaseDataResponse<List<Category>>>> category();
    //14. 自选
    @GET("app/search.ashx")
    Observable<Response<BaseDataResponse<List<ComAll>>>> search(@Query("t") String t,
                                                                @Query("q") String q,
                                                                @Query("p") String p);

    //15. ISBN
    @GET("app/isbn.ashx")
    Observable<Response<BaseDataResponse<ComAll>>> isbn(@Query("isbn") String isbn);
}
