package fm.qian.michael.ui.fragment;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.hr.bclibrary.utils.DisplayUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseRecycleScFragment;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.UserInforConfig;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.UserInfo;
import fm.qian.michael.net.entry.response.Ver;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.activity.SetActivity;
import fm.qian.michael.ui.activity.UserAcrtivity;
import fm.qian.michael.ui.activity.dim.CollectionToBuyActivity;
import fm.qian.michael.ui.activity.dim.DownActivity;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.PlayListMessageAtivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.utils.NotificationUitls;
import fm.qian.michael.utils.SPUtils;
import fm.qian.michael.widget.RoundedImage.RoundedImageView;
import fm.qian.michael.widget.pop.CustomPopuWindConfig;
import fm.qian.michael.widget.pop.PopLoginWindow;
import fm.qian.michael.widget.single.DownManger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.base.fragment.BaseRecycleScFragment;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.PlayListMessageAtivity;
import fm.qian.michael.widget.single.UserInfoManger;

import static fm.qian.michael.ui.activity.UserAcrtivity.USERA;
import static fm.qian.michael.ui.activity.dim.HeadGroupActivity.HEADGROUP;
import static fm.qian.michael.ui.activity.dim.PlayListMessageAtivity.PLAYLIST;
import static fm.qian.michael.ui.activity.dim.PlayListMessageAtivity.PLAYLISTNAME;

/**
 * Created by 吕 on 2017/12/6.
 * 我的
 */

public class MyFragment extends BaseRecycleViewFragment implements View.OnClickListener{

    private final static String ONE = "https://cdn.qian.fm/upload/files/20170615/f624259e95f24e3cbbe43848934338a8.mp3";
    private final static String TWO = "https://cdn.qian.fm/upload/files/20170105/1a51cd1ae6f745e2ae1e954ffcc2d8dd.mp3";
    private final static String THREE = "https://cdn.qian.fm/upload/files/20170105/d35fb53e5d624b5ab75a75f1dc15f9d0.mp3";

    private PopLoginWindow popLoginWindow;

    private QuickAdapter quickAdapter;

    RoundedImageView roundedImageView;
    TextView nickname,tv_bind_wx;

    LinearLayout wodexinxi_layout,baobaoxinxi_layout,
            sczj_layout,ygmzj_layout,bdwx_layout,sz_layout,user_layout,xz_layout;

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(),UserAcrtivity.class);

        switch (view.getId()) {
            case R.id.user_layout://登陆

                if(isLogin()){
                    NToast.shortToastBaseApp(getString(R.string.已登录));
                    return;
                }else {
                    WLoaignMake(false);
                }

                break;
            case R.id.wodexinxi_layout://我的信息
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                intent.setClass(getContext(),UserAcrtivity.class);
                intent.putExtra(USERA,1);
                startActivity(intent);
                break;
            case R.id.baobaoxinxi_layout://宝宝信息
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                intent.setClass(getContext(),UserAcrtivity.class);
                intent.putExtra(USERA,2);
                startActivity(intent);
                break;
            case R.id.sczj_layout://收藏的专辑
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                intent.setClass(mFontext,CollectionToBuyActivity.class);
                intent.putExtra(CollectionToBuyActivity.COLLECTIONTOBUY,GlobalVariable.ONE);
                startActivity(intent);
                break;
            case R.id.ygmzj_layout://已购买专辑
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                intent.setClass(mFontext,CollectionToBuyActivity.class);
                intent.putExtra(CollectionToBuyActivity.COLLECTIONTOBUY,GlobalVariable.TWO);
                startActivity(intent);
                break;
            case R.id.bdwx_layout://绑定微信
                if(!isLogin()){
                    WLoaignMake();
                    return;
                }
                if(GlobalVariable.ONE.equals(UserInfoManger.getInstance().getBindWx())){
                    NToast.shortToastBaseApp(getString(R.string.已绑定));
                }else {

                    CommonUtils.weixinLogin(mFontext,GlobalVariable.ONE,false);
                }

                break;
            case R.id.sz_layout://设置
//                if(!isLogin()){
//                    WLoaignMake();
//                    return;
//                }
                intent.setClass(getContext(),SetActivity.class);
                startActivity(intent);
                break;
            case R.id.xz_layout:
                intent.setClass(getContext(),DownActivity.class);
                startActivity(intent);
                break;
        }


    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initWidget(View view) {
        super.initWidget(view);
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void init() {
        super.init();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_my,null,false);
        roundedImageView = view.findViewById(R.id.head_portrait);
        nickname = view.findViewById(R.id.nickname);
        tv_bind_wx = view.findViewById(R.id.tv_bind_wx);


        user_layout = view.findViewById(R.id.user_layout);
        wodexinxi_layout = view.findViewById(R.id.wodexinxi_layout);
        baobaoxinxi_layout = view.findViewById(R.id.baobaoxinxi_layout);
        sczj_layout = view.findViewById(R.id.sczj_layout);
        ygmzj_layout = view.findViewById(R.id.ygmzj_layout);
        bdwx_layout = view.findViewById(R.id.bdwx_layout);
        sz_layout = view.findViewById(R.id.sz_layout);
        xz_layout = view.findViewById(R.id.xz_layout);

        user_layout.setOnClickListener(this);
        wodexinxi_layout.setOnClickListener(this);
        baobaoxinxi_layout.setOnClickListener(this);
        sczj_layout.setOnClickListener(this);
        ygmzj_layout.setOnClickListener(this);
        bdwx_layout.setOnClickListener(this);
        sz_layout.setOnClickListener(this);
        xz_layout.setOnClickListener(this);



        getRvList().setLayoutManager(new LinearLayoutManager(getContext()));

        quickAdapter = new QuickAdapter(R.layout.item_my_play){
            @Override
            protected void convert(BaseViewHolder helper, Object item) {
                if(item instanceof ComAll){
                    ComAll comAll = (ComAll) item;

                    GlideUtil.setGlideImage(helper.itemView.getContext(),comAll.getCover(),
                            (ImageView) helper.getView(R.id.item_image),R.mipmap.logo);

                    helper.setText(R.id.nickname,comAll.getTitle());

                    helper.setText(R.id.number_tv,comAll.getNums());

                }


            }
        };
        quickAdapter.addHeaderView(view);
        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Object o = quickAdapter.getItem(position);
                if(o instanceof ComAll){
                    ComAll comAll = (ComAll) o;

                    Intent intent = new Intent(getContext(), PlayListMessageAtivity.class);
                    intent.putExtra(PLAYLIST,comAll.getId());
                    intent.putExtra(PLAYLISTNAME,comAll.getTitle());

                    startActivity(intent);
                }



            }
        });

        getRvList().setAdapter(quickAdapter);

    }

    @Override
    public void loadData() {
        super.loadData();
      //  Ver();
        initMesage();//初始化时
        if(isLogin()){
            pageNo = 1;
            isUpOrDown= false;
            setUserInfo("list",pageNo+ "");
        }

    }

    @Override
    public boolean isDamp() {
        return false;
    }

    @Override
    public boolean isJYLogin() {
        return true;
    }

    @Override
    public void loadMore() {
            setUserInfo("list",pageNo+ "");
    }

    @Override
    public void Refresh() {

        initMesage();
        pageNo = 1;
        setUserInfo("list",pageNo+ "");

    }

    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynLogMEvent(Event.LoginEvent event) {

        String id = event.getId();

        if(GlobalVariable.ZERO.equals(id)){
            initMesage();//数据变更时
        }else if(GlobalVariable.ONE.equals(id)){
            pageNo = 1;
            isUpOrDown= false;
            if(isLogin()){
                setUserInfo("list",pageNo+ "");
            }
        }else if(GlobalVariable.TWO.equals(id)){
            initMesage();

            pageNo = 1;
            isUpOrDown= false;
            setUserInfo("list",pageNo+ "");
        };

    }

    private void initMesage(){
        if(isLogin()){

            if(GlobalVariable.ONE.equals(UserInfoManger.getInstance().getBindWx())){
                tv_bind_wx.setText(getString(R.string.已绑定));
                tv_bind_wx.setTextColor(ContextCompat.getColor(mFontext,R.color.color_636));
            }else {
                tv_bind_wx.setText(getString(R.string.未绑定));
                tv_bind_wx.setTextColor(ContextCompat.getColor(mFontext,R.color.color_F86));
            }
            user_infoGet();
        }
    }


    private void user_infoGet(){

        UserInfo userInfo = new UserInfo();

        userInfo.setAct("get");
        userInfo.setUsername(UserInfoManger.getInstance().getUserName());
        userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());


        baseService.user_info(userInfo, new HttpCallback<UserInfo, BaseDataResponse<UserInfo>>() {
            @Override
            public void onNotNet() {
                super.onNotNet();
                GlideUtil.setGlideImage(mFontext
                        , UserInfoManger.getInstance().getLogo()
                        ,roundedImageView,R.drawable.myicon);
                if(!CheckUtil.isEmpty(UserInfoManger.getInstance().getNickName())){
                    nickname.setText(UserInfoManger.getInstance().getNickName());
                }else {
                    nickname.setText(UserInfoManger.getInstance().getUserName());
                }
            }

            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(UserInfo userInfo) {
               // NToast.shortToastBaseApp("成功");

                GlideUtil.setGlideImage(mFontext
                        , userInfo.getLogo()
                        ,roundedImageView,R.drawable.myicon);
                if(!CheckUtil.isEmpty(userInfo.getNickname())){
                    nickname.setText(userInfo.getNickname());
                }else {
                    nickname.setText(UserInfoManger.getInstance().getUserName());
                }

                SPUtils.putString(UserInforConfig.USERLOGO,userInfo.getLogo(),false);
                SPUtils.putString(UserInforConfig.USERNICKNAME,userInfo.getNickname(),false);

            }
        }.setContext(mFontext),MyFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }


    private void setUserInfo(String act,String p){

        UserInfo data = new UserInfo();
        data.setAct(act);
        data.setUsername(UserInfoManger.getInstance().getUserName());
        data.setSessionkey(UserInfoManger.getInstance().getSessionkey());
        if(null != p)
        data.setP(p);

        user_broadcastall(data);
    }

    private void user_broadcastall(UserInfo data){

        baseService.user_broadcastall(
                data,
                new HttpCallback<List<ComAll>, BaseDataResponse<List<ComAll>>>() {
                    @Override
                    public void onNotNet() {
                        if(isUpOrDown){
                            getRefreshLayout().finishLoadMore();
                        }else {
                            getRefreshLayout().finishRefresh();
                        }
                    }

                    @Override
                    public void onError(HttpException e) {
                        NToast.shortToastBaseApp(e.getMsg());

                        if(isUpOrDown){
                            getRefreshLayout().finishLoadMore();
                        }else {
                            getRefreshLayout().finishRefresh();
                        }
                    }

                    @Override
                    public void onSuccess(List<ComAll> comAlls) {

                        if(isUpOrDown){

                            getRefreshLayout().finishLoadMore();
                            if(!CheckUtil.isEmpty(comAlls)){
                                pageNo ++ ;
                                quickAdapter.addData(comAlls);
                            }

                        }else {

                            getRefreshLayout().finishRefresh();
                            if(!CheckUtil.isEmpty(comAlls)){
                                pageNo ++ ;
                                quickAdapter.replaceData(comAlls);
                            }else {
                                quickAdapter.replaceData(new ArrayList<>());
                            }
                        }

                    }
                }.setContext(mFontext),MyFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW)

        );

    }


    private void Ver(){
        baseService.ver(new HttpCallback<Ver, BaseDataResponse<Ver>>() {
            @Override
            public void onError(HttpException e) {

            }

            @Override
            public void onSuccess(Ver ver) {
                NLog.e(NLog.TAG,""+ver.getVer() + ver.getVerint());
            }

        },MyFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }
}
