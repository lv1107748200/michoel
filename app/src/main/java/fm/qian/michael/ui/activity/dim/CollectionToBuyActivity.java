package fm.qian.michael.ui.activity.dim;


import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.base.activity.BaseRecycleViewActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.UserInfo;
import fm.qian.michael.net.entry.response.YZMOrSID;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.single.UserInfoManger;

import static fm.qian.michael.utils.DisplayUtils.ImageHight3;

/*
 * lv   2018/9/13  收藏  购买
 */
public class CollectionToBuyActivity extends BaseRecycleViewActivity {
    public static final String COLLECTIONTOBUY = "CollectionToBuy";

    private String type;
    private boolean isDowp = true;

    private QuickAdapter quickAdapterIma;

    @Override
    public int getLayout() {
        return R.layout.activity_collection_buy;
    }

    @Override
    public void initTitle() {
        type = getIntent().getStringExtra(COLLECTIONTOBUY);

        if(GlobalVariable.ONE.equals(type)){//收藏
            setTitleTv("收藏的专辑");
            isDowp = false;
        }else if(GlobalVariable.TWO.equals(type)){//已购买
            setTitleTv("已购买专辑");
            isDowp = true;
        }

    }

    @Override
    public void initView() {
        super.initView();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        getRvList().setLayoutManager(new LinearLayoutManager(this));

        quickAdapterIma =  new QuickAdapter(R.layout.item_image_and_text,10){
            @Override
            protected void convert(BaseViewHolder helper, Object item) {

                helper.setGone(R.id.item_tv,false);
                LayoutParmsUtils.setHight(ImageHight3(),helper.getView(R.id.item_image));
                if(item instanceof ComAll){

                    GlideUtil.setGlideImageMake(helper.itemView.getContext(),((ComAll) item).getCover(),
                            (ImageView) helper.getView(R.id.item_image));

                }

            }
        };
        quickAdapterIma.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Object item = quickAdapterIma.getItem(position);

                if(item instanceof  ComAll){
                    Intent intent = new Intent(CollectionToBuyActivity.this,HeadGroupActivity.class);
                    intent.putExtra(HeadGroupActivity.HEADGROUP,((ComAll) item).getId());
                    startActivity(intent);
                }

            }
        });

        getRvList().setAdapter(quickAdapterIma);

        if(isDowp){

            View footView = LayoutInflater.from(this).inflate(R.layout.item_tbgmjl_layout,null,false);

            footView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user_tbxiaoe();
                }
            });

            quickAdapterIma.addFooterView(footView);

            user_album();
        }else {
            View headView = LayoutInflater.from(this).inflate(R.layout.item_head_tbgmjl_layout,null,false);

            quickAdapterIma.addHeaderView(headView);
            user_favorite_list();
        }

    }

    @Override
    public boolean isDamp() {
        return isDowp;
    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void Refresh() {
        pageNo = 1;
        user_favorite_list();
    }

    @Override
    public void loadMore() {
        user_favorite_list();
    }

    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynEvent(Event.FavEvent event) {

        int sign = event.getI();

        if(sign == 1){
           getRefreshLayout().autoRefresh();

        }else if(sign == 2){

            List list = quickAdapterIma.getData();

            if(!CheckUtil.isEmpty(list)){
                for(int i = 0; i < list.size(); i++){
                    Object o = list.get(i);

                    if(o instanceof  ComAll){
                       if(((ComAll) o).getId().equals(event.getId())){
                           quickAdapterIma.remove(i);
                           break;
                       }
                    }
                }
            }

        }

    }

    //收藏的专辑
    private void user_favorite_list(){

        UserInfo userInfo = new UserInfo();

        userInfo.setAct("list");
        userInfo.setUsername(UserInfoManger.getInstance().getUserName());
        userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());
        userInfo.setP(pageNo+"");

        baseService.user_favorite_list(userInfo, new HttpCallback<List<ComAll>, BaseDataResponse<List<ComAll>>>() {
                    @Override
                    public void onNotNet() {
                        super.onNotNet();
                        if(isUpOrDown){
                            getRefreshLayout().finishLoadMore();
                        }else {
                            getRefreshLayout().finishRefresh();
                        }
                    }

                    @Override
                    public void onError(HttpException e) {
                     CollectionToBuyActivity.this.onError(e);
                    }

                    @Override
                    public void onSuccess(List<ComAll> comAlls) {
                        if(isUpOrDown){
                            getRefreshLayout().finishLoadMore();
                            if(!CheckUtil.isEmpty(comAlls)){
                                pageNo ++ ;
                                quickAdapterIma.addData(comAlls);
                            }

                        }else {

                            getRefreshLayout().finishRefresh();
                            if(!CheckUtil.isEmpty(comAlls)){
                                pageNo ++ ;
                                quickAdapterIma.replaceData(comAlls);
                            }else {
                                quickAdapterIma.replaceData(new ArrayList<>());
                            }

                        }
                    }
                }.setContext(this),
                CollectionToBuyActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    //购买的专辑
    private void user_album(){

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(UserInfoManger.getInstance().getUserName());
        userInfo.setSessionkey(UserInfoManger.getInstance().getSessionkey());

        baseService.user_album(userInfo, new HttpCallback<List<ComAll>,
                BaseDataResponse<List<ComAll>>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(List<ComAll> comAlls) {

                if(!CheckUtil.isEmpty(comAlls)){
                    quickAdapterIma.replaceData(comAlls);
                }else {
                    quickAdapterIma.replaceData(new ArrayList<>());
//                    quickAdapterIma.setEmptyView(getEmpty());
                }

            }
        }.setContext(this),CollectionToBuyActivity.this.bindUntilEvent(ActivityEvent.DESTROY));

    }

    //同步购买记录
    private void user_tbxiaoe(){
        baseService.user_tbxiaoe(UserInfoManger.getInstance().getSessionkey()
                , UserInfoManger.getInstance().getUserName(), new HttpCallback<Object, BaseDataResponse>() {
                    @Override
                    public void onError(HttpException e) {

                        NToast.shortToastBaseApp(e.getMsg());

                    }

                    @Override
                    public void onSuccessAll(BaseDataResponse k) {

                        user_album();

                    }
                }.setContext(this),CollectionToBuyActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }



}
