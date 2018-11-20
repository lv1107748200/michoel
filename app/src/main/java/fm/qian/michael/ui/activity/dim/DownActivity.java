package fm.qian.michael.ui.activity.dim;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.trello.rxlifecycle2.android.ActivityEvent;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.db.DownTasksModel;
import com.xxbm.sbecomlibrary.db.DownTasksModelAndComAll;
import com.xxbm.sbecomlibrary.db.DownTasksModelAndComAll_Table;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import fm.qian.michael.ui.adapter.DownFragmentAdater;
import fm.qian.michael.ui.fragment.DownAlbumFragment;
import fm.qian.michael.ui.fragment.DownStoryFragment;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.widget.custom.DownTabLayout;
import fm.qian.michael.widget.custom.XCViewPager;
import fm.qian.michael.widget.dialog.LoadingDialog;
import fm.qian.michael.widget.single.DownManger;
import fm.qian.michael.widget.swipemenulib.CstViewPager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
 * lv   2018/11/6
 */
public class DownActivity extends BaseIntensifyActivity {

    private DownFragmentAdater downFragmentAdater;
    private List<ComAll> comAllList;

    @BindView(R.id.magic_indicator)
    public DownTabLayout magicIndicator;
    @BindView(R.id.xViewPager_main)
    CstViewPager xViewPagerMain;



    @Override
    public int getLayout() {
        return R.layout.activity_down;
    }

    @Override
    public void initView() {
        super.initView();

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setTitleTv("我的下载");
        final String[] id = {"故事","专辑"};


        magicIndicator.setDownTabView(Arrays.asList(id), new DownTabLayout.DownCallBack() {
            @Override
            public void selCallBack(int num) {
                xViewPagerMain.setCurrentItem(num);
            }
        });
        magicIndicator.selectDownTab(0);

        downFragmentAdater = new DownFragmentAdater(getSupportFragmentManager());
        //xViewPagerMain.setScrollable(true);
        xViewPagerMain.setAdapter(downFragmentAdater);

        List<Fragment> fragmentList = new ArrayList<>();

        fragmentList.add(new DownStoryFragment());
        fragmentList.add(new DownAlbumFragment());

        downFragmentAdater.setTitles(fragmentList);


        xViewPagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.selectDownTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void initData() {
        super.initData();
        getData();
    }

    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynEvent(Event.UpDownEvent event) {

        int i = event.getI();

        if(i == 1){

            if(!CheckUtil.isEmpty(comAllList)){
                if(!CheckUtil.isEmpty(event.getComAlls())){
                    getUpDownData(event.getComAlls());
                }
            }

        }else if(i == 0){
            showLoadingDialog("数据处理中");
        }else if(i == 3){

        }

    }

    private void getUpDownData(
       final     List<ComAll> upCom
    ){


        Observable o = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {

                Iterator<ComAll> iterator = comAllList.iterator();

                while (iterator.hasNext()) {
                    ComAll value = iterator.next();
                    for(ComAll comAll : upCom){
                        if(value.getId().equals(comAll.getId())){
                            iterator.remove();
                        }
                    }
                }


                DownGather downGather = new DownGather();

               // int albumNum = 0;

                Map<String,DownTasksModel> tasksModelMap = new HashMap<>();

                for(int i =0, j= comAllList.size(); i<j; i++){

                    ComAll comAll = comAllList.get(i);

                    List<DownTasksModelAndComAll> downTasksModels =   SQLite.select()
                            .from(DownTasksModelAndComAll.class)
                            .where(DownTasksModelAndComAll_Table.comAll_id.eq(comAll.getId())).queryList();


                    if(!CheckUtil.isEmpty(downTasksModels)){
                       // albumNum ++;

                        for(DownTasksModelAndComAll modelComAll : downTasksModels){

                            DownTasksModel downTasksModel = modelComAll.getDownTasksModel();

                            if(null != downTasksModel){

                                DownTasksModel downTasksModelMap = tasksModelMap.get(downTasksModel.getId());

                                if(downTasksModelMap != null){
                                    downTasksModelMap.setComAll(comAll);
                                }else {
                                    downTasksModel.setComAll(comAll);
                                    tasksModelMap.put(downTasksModel.getId(),downTasksModel);
                                }
                            }
                        }
                    }
                }

                List<DownTasksModel> downTasksModelList = new ArrayList<>();

                for (Map.Entry<String, DownTasksModel> entry : tasksModelMap.entrySet()) {
                    downTasksModelList.add(entry.getValue());
                }

                downGather.setComAllNum(comAllList.size()+"");
                downGather.setAlbumNum(downTasksModelList.size()+"");

                downGather.setComAllListCompleted(comAllList);
                downGather.setDownTasksModelList(downTasksModelList);
                emitter.onNext(downGather);
            }
        });

        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<DownGather>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DownGather o) {

                        if(null != o){
                            NLog.e(NLog.TAGDOWN, "故事下载 数据处理后 " + o.getDownTasksModelList().size());
                        }
                        DownStoryFragment downStoryFragment = (DownStoryFragment) downFragmentAdater.getItem(0);
                        downStoryFragment.setQuickAdapter(o.comAllListCompleted);
                        if(!CheckUtil.isEmpty(o.getComAllListCompleted())){
                            magicIndicator.setNum(0,o.comAllNum);
                        }else {
                            magicIndicator.setNum(0,"");
                        }


                        DownAlbumFragment downAlbumFragment = (DownAlbumFragment) downFragmentAdater.getItem(1);
                        downAlbumFragment.setQuickAdapter(o.downTasksModelList);
                        if(!CheckUtil.isEmpty(o.getDownTasksModelList())){
                            magicIndicator.setNum(1,o.albumNum);
                        }else {
                            magicIndicator.setNum(1,"");
                        }


                        dissLoadingDialog();

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });



    }


    private void getData(){
        showLoadingDialog("加载数据");

        Observable o = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                List<ComAll> comAllListCompleted = new ArrayList<>();
                DownGather downGather = new DownGather();

                List<ComAll> tResult = SQLite.select()
                        .from(ComAll.class).queryList();

                if(!CheckUtil.isEmpty(tResult)){//得到已经下载完成的故事

                    for(int i=0 , j= tResult.size(); i<j; i++){
                        ComAll rankMore = tResult.get(i);
                        String path;
                        int id;

                        if(CheckUtil.isEmpty(rankMore.getDownPath())){

                            path = DownManger.createPath(rankMore.getUrl());
                            id = FileDownloadUtils.generateId(rankMore.getUrl(), path);
                            rankMore.setDownPath(path);
                            rankMore.setIsDown(id);

                        }else {

                            path = rankMore.getDownPath();
                            id = rankMore.getIsDown();

                        }

                        int statue = DownManger.isDownStatus(id, path);

                        if(statue == FileDownloadStatus.completed){
                            comAllListCompleted.add(rankMore);
                        }else {
                           // comAllListCompleted.add(rankMore);
                        }

                    }

                }

               // int albumNum = 0;

                Map<String,DownTasksModel> tasksModelMap = new HashMap<>();

                for(int i =0, j= comAllListCompleted.size(); i<j; i++){

                    ComAll comAll = comAllListCompleted.get(i);

                    List<DownTasksModelAndComAll> downTasksModels =   SQLite.select()
                            .from(DownTasksModelAndComAll.class)
                            .where(DownTasksModelAndComAll_Table.comAll_id.eq(comAll.getId())).queryList();


                    if(!CheckUtil.isEmpty(downTasksModels)){
                      //  albumNum ++;

                        for(DownTasksModelAndComAll modelComAll : downTasksModels){

                            DownTasksModel downTasksModel = modelComAll.getDownTasksModel();

                            if(null != downTasksModel){

                                DownTasksModel downTasksModelMap = tasksModelMap.get(downTasksModel.getId());

                                if(downTasksModelMap != null){
                                    downTasksModelMap.setComAll(comAll);
                                }else {
                                    downTasksModel.setComAll(comAll);
                                    tasksModelMap.put(downTasksModel.getId(),downTasksModel);
                                }
                            }
                        }
                    }
                }

                List<DownTasksModel> downTasksModelList = new ArrayList<>();

                for (Map.Entry<String, DownTasksModel> entry : tasksModelMap.entrySet()) {
                    downTasksModelList.add(entry.getValue());
                }

                comAllList = comAllListCompleted;

                downGather.setComAllNum(comAllListCompleted.size()+"");
                downGather.setAlbumNum(downTasksModelList.size()+"");

                downGather.setComAllListCompleted(comAllListCompleted);
                downGather.setDownTasksModelList(downTasksModelList);

                emitter.onNext(downGather);
            }
        });

        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<DownGather>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DownGather o) {

                        if(null != o){
                            NLog.e(NLog.TAGDOWN, "故事下载 数据处理后 " + o.getDownTasksModelList().size());
                        }

                        if(!CheckUtil.isEmpty(o.getComAllListCompleted())){

                            DownStoryFragment downStoryFragment = (DownStoryFragment) downFragmentAdater.getItem(0);

                            downStoryFragment.setQuickAdapter(o.comAllListCompleted);

                            magicIndicator.setNum(0,o.comAllNum);
                        }

                        if(!CheckUtil.isEmpty(o.getDownTasksModelList())){
                            DownAlbumFragment downAlbumFragment = (DownAlbumFragment) downFragmentAdater.getItem(1);

                            downAlbumFragment.setQuickAdapter(o.downTasksModelList);

                            magicIndicator.setNum(1,o.albumNum);
                        }


                        dissLoadingDialog();

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static class DownGather{
        private String comAllNum;
        private String albumNum;
        private List<ComAll> comAllListCompleted;
        private List<DownTasksModel> downTasksModelList;

        public List<ComAll> getComAllListCompleted() {
            return comAllListCompleted;
        }

        public void setComAllListCompleted(List<ComAll> comAllListCompleted) {
            this.comAllListCompleted = comAllListCompleted;
        }

        public List<DownTasksModel> getDownTasksModelList() {
            return downTasksModelList;
        }

        public void setDownTasksModelList(List<DownTasksModel> downTasksModelList) {
            this.downTasksModelList = downTasksModelList;
        }

        public String getComAllNum() {
            return comAllNum;
        }

        public void setComAllNum(String comAllNum) {
            this.comAllNum = comAllNum;
        }

        public String getAlbumNum() {
            return albumNum;
        }

        public void setAlbumNum(String albumNum) {
            this.albumNum = albumNum;
        }
    }

    //    final String[] id = {"故事   ","专辑    "};
//    downFragmentAdater = new DownFragmentAdater(getSupportFragmentManager());
//    //xViewPagerMain.setScrollable(true);
//        xViewPagerMain.setAdapter(downFragmentAdater);
//
//    List<Fragment> fragmentList = new ArrayList<>();
//
//        fragmentList.add(new DownStoryFragment());
//        fragmentList.add(new DownAlbumFragment());
//
//        downFragmentAdater.setTitles(fragmentList);
//
//    CommonNavigator commonNavigator = new CommonNavigator(this);
//        commonNavigator.setScrollPivotX(0.25f);
//        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
//        @Override
//        public int getCount() {
//            return id.length;
//        }
//
//        @Override
//        public IPagerTitleView getTitleView(Context context, final int index) {
//            SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
//            simplePagerTitleView.setText(id[index]);
//            simplePagerTitleView.setNormalColor(Color.parseColor("#292B2F"));
//            simplePagerTitleView.setSelectedColor(Color.parseColor("#F86E78"));
//            simplePagerTitleView.setTextSize(15);
//            simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    NLog.e(NLog.TAGOther,"点击编号 --->" + index);
//
//                    xViewPagerMain.setCurrentItem(index);
//                }
//            });
//            return simplePagerTitleView;
//        }
//
//        @Override
//        public IPagerIndicator getIndicator(Context context) {
//            LinePagerIndicator indicator = new LinePagerIndicator(context);
//            indicator.setXOffset(DisplayUtils.getDimen(R.dimen.margin_6));
//            indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
//            indicator.setColors(Color.parseColor("#F86E78"));
//            indicator.setRoundRadius(DisplayUtils.getDimen(R.dimen.radius));
//            return indicator;
//        }
//    });
//        magicIndicator.setNavigator(commonNavigator);
//        ViewPagerHelper.bind(magicIndicator, xViewPagerMain);
}
