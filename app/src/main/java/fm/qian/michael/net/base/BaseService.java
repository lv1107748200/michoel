package fm.qian.michael.net.base;





import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONObject;

import fm.qian.michael.base.BaseApplation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import fm.qian.michael.net.Service.AppService;
import fm.qian.michael.net.Service.UserService;
import fm.qian.michael.net.Service.WXUserService;
import fm.qian.michael.net.entry.request.Reg;
import fm.qian.michael.net.entry.response.Album;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.Category;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.Index;
import fm.qian.michael.net.entry.response.UserInfo;
import fm.qian.michael.net.entry.response.Ver;
import fm.qian.michael.net.entry.response.WXAccessData;
import fm.qian.michael.net.entry.response.YZMOrSID;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpUtils;
import fm.qian.michael.net.subscriber.HttpSubscriber;
import fm.qian.michael.net.subscriber.HttpUserSubscriber;
import fm.qian.michael.net.subscriber.HttpWXUserSubscriber;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by 吕 on 2017/10/27.
 */

public class BaseService {

    public BaseService() {
        BaseApplation.getBaseApp().getAppComponent().inject(this);
    }
    @Inject
    public AppService appService;
    @Inject
    public WXUserService wxuserService;
    @Inject
    public UserService userService;
    //1 版本信息
    @SuppressWarnings("unchecked")
    public void ver(
            HttpCallback<Ver,BaseDataResponse<Ver>> httpCallback
    ,ObservableTransformer transformer
    ) {

        Observable observable =  appService.ver();
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<Ver>>(httpCallback)
                ,transformer
        );
    }

    //2.首页
    @SuppressWarnings("unchecked")
    public void index(
            HttpCallback<Index,BaseDataResponse<Index>> httpCallback
            ,ObservableTransformer transformer
    ) {

        Observable observable =  appService.index();
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<Index>>(httpCallback)
                ,transformer
        );
    }
    //3.全部排行榜
    @SuppressWarnings("unchecked")
    public void ranklist(
            String tid,String day,String p,
            HttpCallback<List<ComAll>, BaseDataResponse<List<ComAll>>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.ranklist(tid,day,p);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<List<ComAll>>>(httpCallback)
                ,transformer
        );
    }
    //4.首页更多推荐 p 页码
    @SuppressWarnings("unchecked")
    public void recommend(
            String p,
            HttpCallback<List<Base>, BaseDataResponse<List<Base>>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.recommend(p);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<List<Base>>>(httpCallback)
                ,transformer
        );
    }
    //5.专题
    @SuppressWarnings("unchecked")
    public void topic(
            String id,
            String p,
            HttpCallback<ComAll,BaseResponse<ComAll,List<Base>>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.topic(id,p);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseResponse<ComAll,List<Base>>>(httpCallback)
                ,transformer
        );
    }
    //6.专辑
    @SuppressWarnings("unchecked")
    public void album(
            String id,
            String p,
            String phone,
            String sessionkey,
            HttpCallback<Album,BaseResponse<Album,List<ComAll>>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.album(id,p,phone,sessionkey);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseResponse<Album,List<ComAll>>>(httpCallback)
                ,transformer
        );
    }
    //7.音频
    @SuppressWarnings("unchecked")
    public void audio(
            String id,
            String phone,
            String sessionkey,
            HttpCallback<ComAll, BaseDataResponse<ComAll>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.audio(id,phone,sessionkey);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<ComAll>>(httpCallback)
                ,transformer
        );
    }
    //8.播放次数上报
    @SuppressWarnings("unchecked")
    public void pta(
            HttpCallback<ComAll, BaseDataResponse> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.pta();
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse>(httpCallback)
                ,transformer
        );
    }

    //9.音频
    @SuppressWarnings("unchecked")
    public void video(
            String id,
            HttpCallback<ComAll, BaseDataResponse<ComAll>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.video(id);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<ComAll>>(httpCallback)
                ,transformer
        );
    }
    //10. 图文
    @SuppressWarnings("unchecked")
    public void article(
            String id,
            HttpCallback<ComAll, BaseDataResponse<ComAll>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.article(id);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<ComAll>>(httpCallback)
                ,transformer
        );
    }
    //11. 文章
    @SuppressWarnings("unchecked")
    public void articlelist(
            String q,
            String p,
            HttpCallback<List<Base>, BaseDataResponse<List<Base>>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.articlelist(q,p);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<List<Base>>>(httpCallback)
                ,transformer
        );
    }
    //12. 搜索热门关键词
    @SuppressWarnings("unchecked")
    public void search_hotwords(
            String tid,
            HttpCallback<List<Base>, BaseDataResponse<List<Base>>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.search_hotwords(tid);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<List<Base>>>(httpCallback)
                ,transformer
        );
    }
    //13. 分类
    @SuppressWarnings("unchecked")
    public void category(
            HttpCallback<List<Category>, BaseDataResponse<List<Category>>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.category();
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<List<Category>>>(httpCallback)
                ,transformer
        );
    }
    //14. 自选
    @SuppressWarnings("unchecked")
    public void search(
            String t,
            String q,
            String p,
            HttpCallback<List<ComAll>, BaseDataResponse<List<ComAll>>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.search(t,q,p);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<List<ComAll>>>(httpCallback)
                ,transformer
        );
    }
    //15 ISBN
    @SuppressWarnings("unchecked")
    public void isbn(
            String isbn,
            HttpCallback<ComAll, BaseDataResponse<ComAll>> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  appService.isbn(isbn);
        HttpUtils.toSubscribe(
                observable,
                new HttpSubscriber<BaseDataResponse<ComAll>>(httpCallback)
                ,transformer
        );
    }


//    以下为 用户接口

  //登陆
  @SuppressWarnings("unchecked")
  public void login(
          Reg data,
          HttpCallback<UserInfo,BaseDataResponse<UserInfo>> httpCallback
          , ObservableTransformer transformer){

//      RequestBody body = HttpUtils.buildRequestBody(data);
      String body = HttpUtils.getStringValue(data);

      Map mapType = HttpUtils.jsonToBean(body,Map.class);
      Observable observable =  userService.user_login(mapType);

      HttpUtils.toSubscribe(
              observable,
              new HttpUserSubscriber<BaseDataResponse<UserInfo>>(httpCallback)
              ,transformer
      );

  }
    //注册
      @SuppressWarnings("unchecked")
      public void user_reg(
              Reg data,
              HttpCallback<UserInfo,BaseDataResponse<UserInfo>> httpCallback
              , ObservableTransformer transformer){

//          RequestBody body = HttpUtils.buildRequestBody(data);
          String body = HttpUtils.getStringValue(data);

          Map mapType = HttpUtils.jsonToBean(body,Map.class);
          Observable observable =  userService.user_reg(mapType);

          HttpUtils.toSubscribe(
                  observable,
                  new HttpUserSubscriber<BaseDataResponse<UserInfo>>(httpCallback)
                  ,transformer
          );

      }

    //获取短信验证码时  首先要获取 sid
    @SuppressWarnings("unchecked")
    public void getyzm(
           String sid
           ,String random
            ,HttpCallback<YZMOrSID, BaseDataResponse<YZMOrSID>> httpCallback
            , ObservableTransformer transformer
    ) {
        Observable observable =  userService.getyzm(sid,random);
        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<YZMOrSID>>(httpCallback)
                ,transformer
        );
    }
    //短信验证码
    @SuppressWarnings("unchecked")
    public void user_sms(
            Reg data,
            HttpCallback<Object,BaseDataResponse> httpCallback
            , ObservableTransformer transformer){

       // RequestBody body = HttpUtils.buildRequestBody(data);
        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);

        Observable observable =  userService.user_sms(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse>(httpCallback)
                ,transformer
        );

    }
    //用户绑定微信
    @SuppressWarnings("unchecked")
    public void user_bind(
            UserInfo data,
            HttpCallback<Object,BaseDataResponse<Object>> httpCallback
            , ObservableTransformer transformer){

        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);
        Observable observable =  userService.user_bind(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<Object>>(httpCallback)
                ,transformer
        );

    }
    //同步购买记录
    @SuppressWarnings("unchecked")
    public void user_tbxiaoe(
            String sessionkey,
            String username,

            HttpCallback<YZMOrSID, BaseDataResponse<UserInfo>> httpCallback
            , ObservableTransformer transformer
    ) {
        Observable observable =  userService.user_tbxiaoe(sessionkey,username);
        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<UserInfo>>(httpCallback)
                ,transformer
        );
    }
    //我的信息 宝宝信息
    @SuppressWarnings("unchecked")
    public void user_info(
            UserInfo data,
            HttpCallback<UserInfo,BaseDataResponse<UserInfo>> httpCallback
            , ObservableTransformer transformer){

//        RequestBody body = HttpUtils.buildRequestBody(data);
        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);
        Observable observable =  userService.user_info(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<UserInfo>>(httpCallback)
                ,transformer
        );

    }

    //上传头像

    @SuppressWarnings("unchecked")
    public void user_logo( UserInfo data,File file, HttpCallback<Object,
            BaseDataResponse> httpCallback , ObservableTransformer transformer) {

        // 创建 RequestBody，用于封装 请求RequestBody
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
// MultipartBody.Part is used to send also the actual file name

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", System.currentTimeMillis()+".jpg", requestFile);
// 添加描述
//        String descriptionString = "{\"userId\":\"10000023\",\"date\":\"2016-11-22\"}";
//        RequestBody description =
//                RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);





        String bodyt = HttpUtils.getStringValue(data);

        Map<String ,String> mapType = HttpUtils.jsonToBean(bodyt,Map.class);

        Map<String, RequestBody> requestBodyMap = new HashMap<>();


        for (String key : mapType.keySet()) {

            RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), mapType.get(key));
            requestBodyMap.put(key,description);
        }

        Observable observable =  userService.user_logo(body,requestBodyMap);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse>(httpCallback)
                ,transformer
        );
    }

    //已购买的专辑
    @SuppressWarnings("unchecked")
    public void user_album(
            UserInfo data,
            HttpCallback<List<ComAll>,BaseDataResponse<List<ComAll>>> httpCallback
            , ObservableTransformer transformer){

        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);

        Observable observable =  userService.user_album(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<List<ComAll>>>(httpCallback)
                ,transformer
        );

    }

    //收藏的专辑
    @SuppressWarnings("unchecked")
    public void user_favorite(
            UserInfo data,
            HttpCallback<Object,BaseDataResponse<Object>> httpCallback
            , ObservableTransformer transformer){

        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);

        Observable observable =  userService.user_favorite(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<Object>>(httpCallback)
                ,transformer
        );

    }

    //收藏的专辑 list
    @SuppressWarnings("unchecked")
    public void user_favorite_list(
            UserInfo data,
            HttpCallback<List<ComAll>,BaseDataResponse<List<ComAll>>> httpCallback
            , ObservableTransformer transformer){

        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);

        Observable observable =  userService.user_favorite_list(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<List<ComAll>>>(httpCallback)
                ,transformer
        );

    }
    //我的播单
    @SuppressWarnings("unchecked")
    public void user_broadcast(
            UserInfo data,
            HttpCallback<ComAll,BaseDataResponse> httpCallback
            , ObservableTransformer transformer){

        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);

        Observable observable =  userService.user_broadcast(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse>(httpCallback)
                ,transformer
        );

    }

    ////我的播单 分页
    @SuppressWarnings("unchecked")
    public void user_broadcastall(
            UserInfo data,
            HttpCallback<List<ComAll>,BaseDataResponse<List<ComAll>>> httpCallback
            , ObservableTransformer transformer){

        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);

        Observable observable =  userService.user_broadcastall(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<List<ComAll>>>(httpCallback)
                ,transformer
        );

    }

    //播单详情列表
    @SuppressWarnings("unchecked")
    public void user_broadcastlist(
            UserInfo data,
            HttpCallback<Object,BaseDataResponse<Object>> httpCallback
            , ObservableTransformer transformer){

        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);

        Observable observable =  userService.user_broadcastlist(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<Object>>(httpCallback)
                ,transformer
        );

    }

    //播单详情列表
    @SuppressWarnings("unchecked")
    public void user_broadcastlistAll(
            UserInfo data,
            HttpCallback<List<ComAll>,BaseDataResponse<List<ComAll>>> httpCallback
            , ObservableTransformer transformer){

        String body = HttpUtils.getStringValue(data);

        Map mapType = HttpUtils.jsonToBean(body,Map.class);

        Observable observable =  userService.user_broadcastlistAll(mapType);

        HttpUtils.toSubscribe(
                observable,
                new HttpUserSubscriber<BaseDataResponse<List<ComAll>>>(httpCallback)
                ,transformer
        );

    }


    //微信获取 access_token
    @SuppressWarnings("unchecked")
    public void access_token(
            String appid,
            String secret,
            String code,
            HttpCallback<WXAccessData,WXAccessData> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  wxuserService.access_token(appid,secret,code,"authorization_code");
        HttpUtils.toSubscribe(
                observable,
                new HttpWXUserSubscriber<WXAccessData>(httpCallback)
                ,transformer
        );
    }

    //微信获取 用户信息
    @SuppressWarnings("unchecked")
    public void userinfo(
            String access_token,
            String openid,
            HttpCallback<WXAccessData,WXAccessData> httpCallback
            , ObservableTransformer transformer
    ) {

        Observable observable =  wxuserService.userinfo(access_token,openid);
        HttpUtils.toSubscribe(
                observable,
                new HttpWXUserSubscriber<WXAccessData>(httpCallback)
                ,transformer
        );
    }

}
