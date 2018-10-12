package fm.qian.michael.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hr.bclibrary.utils.CheckUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;

import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.DataServer;
import fm.qian.michael.net.entry.MultipleItem;
import fm.qian.michael.net.entry.Video;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.Index;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.HeadGroupTopActivity;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.ui.activity.WebParticularsActivity;
import fm.qian.michael.ui.activity.dim.TopListActivity;
import fm.qian.michael.ui.adapter.MultipleItemQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import fm.qian.michael.net.entry.DataServer;
import fm.qian.michael.ui.activity.WebParticularsActivity;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.utils.SPUtils;

import static fm.qian.michael.common.UserInforConfig.USERFIRSTAUDIO;
import static fm.qian.michael.ui.activity.dim.HeadGroupActivity.HEADGROUP;

/**
 * Created by 吕 on 2017/12/6.
 * 发现
 */

public class FindFragment extends BaseRecycleViewFragment {

    private MultipleItemQuickAdapter multipleItemAdapter ;

    private String VidoAll;

    @OnClick({})
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    @Override
    public void initWidget(View view) {
        super.initWidget(view);
   }

    @Override
    public void init() {
        super.init();
        final List<MultipleItem> data = DataServer.getMultipleItemData();
        multipleItemAdapter = new MultipleItemQuickAdapter(getContext(),data);
        final GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        getRvList().setLayoutManager(manager);

        multipleItemAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return data.get(position).getSpanSize();
            }
        });
        multipleItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Intent intent = new Intent();
                MultipleItem multipleItem =    multipleItemAdapter.getData().get(position);
                switch (adapter.getItemViewType(position)) {
                    case MultipleItem.BAN://轮播图

                        break;
                    case MultipleItem.HEAD:// 头部
                        if(true){
                            if(multipleItem.getObject() instanceof Video){

                                if(GlobalVariable.排行榜.equals(((Video) multipleItem.getObject()).getName())){
                                    intent.setClass(mFontext, TopListActivity.class);
                                    startActivity(intent);
                                }else if(GlobalVariable.热门文章.equals(((Video) multipleItem.getObject()).getName())) {
                                    EventBus.getDefault().post(new Event.MainActivityEvent(2));
                                }else if(GlobalVariable.精彩视频.equals(((Video) multipleItem.getObject()).getName())){
                                    intent.setClass(mFontext, HeadGroupTopActivity.class);
                                    intent.putExtra(HeadGroupTopActivity.HEADGROUPOTHER,VidoAll);
                                    startActivity(intent);
                                }

                            }
                        }
                        break;
                    case MultipleItem.IMGANDTEXT://上下结构的 图片 文字
                        if(multipleItem.getObject() instanceof  Base){
                            Base base = (Base)multipleItem.getObject() ;
                            CommonUtils.setIntent(intent,mFontext,base);
                        }

                        break;
                    case MultipleItem.IMGANDTEXTG2://上下结构的 图片 文字
                        if(multipleItem.getObject() instanceof  Base){
                            Base base = (Base)multipleItem.getObject() ;
                            CommonUtils.setIntent(intent,mFontext,base);
                        }
                        break;
                    case MultipleItem.IMGANDTEXTG4://上下结构的 图片 文字

//                        intent.setClass(getContext(), HeadGroupActivity.class);
//                        intent.putExtra(HeadGroupActivity.HEADGROUP,1);
//                        startActivity(intent);

                        if(multipleItem.getObject() instanceof  Base){
                            Base base = (Base)multipleItem.getObject() ;
                            CommonUtils.setIntent(intent,mFontext,base);
                        }


                        break;
                    case MultipleItem.TEXTDATE:// 左右结构的 图片 文字
//                        intent.setClass(getContext(), WebParticularsActivity.class);
//                        startActivity(intent);
                        if(multipleItem.getObject() instanceof  Base){
                            Base base = (Base)multipleItem.getObject() ;
                            CommonUtils.setIntent(intent,mFontext,base);
                        }
                        break;
                    case MultipleItem.RANKING://排行榜

                        break;
                    case MultipleItem.RECY://水平滚动

                        break;
                }


            }
        });

//        getRvList().addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_IDLE){
//                    Glide.with(getContext()).resumeRequests();
//                }else{
//                    Glide.with(getContext()).pauseRequests();
//                }
//            }
//        });

        getRvList().setAdapter(multipleItemAdapter);

    }

    @Override
    public void loadData() {
        super.loadData();
        index();
    }

    @Override
    public void Refresh() {
        index();
    }

    @Override
    public void loadMore() {
        recommend();
    }

    private void index(){
        baseService.index(new HttpCallback<Index, BaseDataResponse<Index>>() {
            @Override
            public void onError(HttpException e) {
                getRefreshLayout().finishRefresh();
                NLog.e(NLog.TAG,e.getMsg());
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(Index index) {
                getRefreshLayout().finishRefresh();

                if(!CheckUtil.isEmpty(index.getFirstaudio())){
                    SPUtils.putString(USERFIRSTAUDIO,index.getFirstaudio(),false);
                }
                pageNo = 1;
                recommend();

                setIndexData(index);

            }
        },FindFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }

    private void setIndexData(Index index){

        VidoAll = index.getVideoall();

        List<MultipleItem> list = new ArrayList<>();

        if(!CheckUtil.isEmpty(index.getSwiper())){
            list.add(new MultipleItem(MultipleItem.BAN, MultipleItem.FOUR,index.getSwiper()));
        }

        if(!CheckUtil.isEmpty(index.getNav())){
            List<Base> bases = index.getNav();
            for(int i = 0,j=bases.size();i<j;i++){
                list.add(new MultipleItem(MultipleItem.IMGANDTEXTG4, MultipleItem.ONE, bases.get(i)));
            }
        }

        if(!CheckUtil.isEmpty(index.getToday())){
            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video(-1, GlobalVariable.今日推荐)));
            List<Base> bases = index.getToday();
            for(int i = 0,j=bases.size();i<j;i++){
                list.add(new MultipleItem(MultipleItem.IMGANDTEXTG2, MultipleItem.TWO, bases.get(i)));
            }
        }

        if(!CheckUtil.isEmpty(index.getNews())){
            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video(R.drawable.article_search,GlobalVariable.热门文章)));
            List<Base> bases = index.getNews();
            for(int i = 0,j=bases.size();i<j;i++){
                list.add(new MultipleItem(MultipleItem.TEXTDATE, MultipleItem.FOUR,  bases.get(i)));
            }
        }

        if(!CheckUtil.isEmpty(index.getTopic())){
            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video(-1,GlobalVariable.主题推荐)));
            list.add(new MultipleItem(MultipleItem.RECY, MultipleItem.FOUR,index.getTopic()));
        }

        if(!CheckUtil.isEmpty(index.getRank())){
            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video(R.drawable.myjt1,GlobalVariable.排行榜)));
            list.add(new MultipleItem(MultipleItem.RANKING, MultipleItem.FOUR,index.getRank()));
        }

        if(!CheckUtil.isEmpty(index.getVideo())){
            list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video( R.drawable.myjt1,GlobalVariable.精彩视频)));
            list.add(new MultipleItem(MultipleItem.RECY, MultipleItem.FOUR,index.getVideo()));
        }

        multipleItemAdapter.replaceData(list);
    }

    private void recommend(){
        baseService.recommend(pageNo+"", new HttpCallback<List<Base>, BaseDataResponse<List<Base>>>() {
            @Override
            public void onError(HttpException e) {
                getRefreshLayout().finishLoadMore();
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(List<Base> baseList) {
                getRefreshLayout().finishLoadMore();
                setRecommend(baseList);
            }
        },FindFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }

    private void setRecommend(List<Base> baseList){

        if(isUpOrDown){
            if(!CheckUtil.isEmpty(baseList)){
                pageNo ++;
                List<MultipleItem> list = new ArrayList<>();
                for(int i = 0,j=baseList.size();i<j;i++){
                    list.add(new MultipleItem(MultipleItem.IMGANDTEXT, MultipleItem.FOUR, baseList.get(i)));
                }

                multipleItemAdapter.addData(list);
            }else {
                getRefreshLayout().finishLoadMoreWithNoMoreData();
            }
        }else {
            if(!CheckUtil.isEmpty(baseList)){
                pageNo ++;
                List<MultipleItem> list = new ArrayList<>();
                list.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.FOUR, new Video(-1,GlobalVariable.更多推荐)));

                for(int i = 0,j=baseList.size();i<j;i++){
                    list.add(new MultipleItem(MultipleItem.IMGANDTEXT, MultipleItem.FOUR, baseList.get(i)));
                }
                multipleItemAdapter.addData(list);
            }
        }


    }
}
