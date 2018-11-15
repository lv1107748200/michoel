package fm.qian.michael.ui.fragment;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseFragment;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.common.BaseDownViewHolder;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.db.AppDatabase;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.ComAll_Table;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.activity.dim.DownActivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.dialog.LoadingDialog;
import fm.qian.michael.widget.single.DownManger;

import static fm.qian.michael.utils.CommonUtils.getNumberIndex;

/*
 * lv   2018/11/6 故事
 */
public class DownStoryFragment extends BaseFragment implements View.OnClickListener{

    private QuickAdapter quickAdapter;
    private boolean isPlay = false;
    private boolean isSelMore = false;//是否多选操作
    private boolean isGetData = false;
    private LoadingDialog loadingDialog;
    private ComAll comAll;
    private List<ComAll> comAllList;
    private List<ComAll> selList;
    private DownActivity downActivity;

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.buttom_layout)
    LinearLayout buttomLayout;
    @BindView(R.id.del_layout)
    LinearLayout del_layout;
    @OnClick({R.id.del_layout,R.id.play_layout})
    public void Onclick(View view){
        switch (view.getId()){
            case R.id.del_layout:

                if(!CheckUtil.isEmpty(selList)){
                    del_layout.setEnabled(false);
                    delSelect();
                }

                break;
            case R.id.play_layout:
                if(!CheckUtil.isEmpty(selList)){
                    CommonUtils.getIntent(mFontext,selList,0, GlobalVariable.THREE);
                }
                break;
        }
    }

    LinearLayout sel_all_Layout,cancel_layout,play_all_Layout,manger_layout;

    RelativeLayout relayout_sel_cancel;
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_down_story;
    }

    @Override
    public void initWidget(View view) {
        super.initWidget(view);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sel_all_Layout://全选
                sel_all_Layout.setSelected(!sel_all_Layout.isSelected());

                if(sel_all_Layout.isSelected()){
                    if(null != selList){
                        selList.clear();
                        selList.addAll(comAllList);
                        buttomLayout.setVisibility(View.VISIBLE);
                    }
                }else {
                    if(!CheckUtil.isEmpty(selList)){
                        selList.clear();
                        buttomLayout.setVisibility(View.GONE);
                    }
                }
                quickAdapter.notifyDataSetChanged();
                break;
            case R.id.cancel_layout://取消
                isSelMore = !isSelMore;

                buttomLayout.setVisibility(View.GONE);
                relayout_sel_cancel.setVisibility(View.GONE);

                if(!CheckUtil.isEmpty(selList)) {
                    selList.clear();
                }
                sel_all_Layout.setSelected(false);
                quickAdapter.notifyDataSetChanged();
                break;
            case R.id.manger_layout://管理
                isSelMore = !isSelMore;

                if(isSelMore){
                    relayout_sel_cancel.setVisibility(View.VISIBLE);
                }else {
                    //buttomLayout.setVisibility(View.GONE);
                }

                quickAdapter.notifyDataSetChanged();
                break;
            case R.id.play_all_Layout://播放全部
                if(!CheckUtil.isEmpty(comAllList)){
                    CommonUtils.getIntent(mFontext,comAllList,0, GlobalVariable.THREE);
                }
                break;
        }
    }

    @Override
    public void init() {
        super.init();
        downActivity = (DownActivity) getActivity();

        loadingDialog = new LoadingDialog(mFontext);
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        selList = new ArrayList<>();

        View  headView = LayoutInflater.from(mFontext).inflate(R.layout.item_make_down,null,false);
        sel_all_Layout = headView.findViewById(R.id.sel_all_Layout);
        cancel_layout = headView.findViewById(R.id.cancel_layout);
        play_all_Layout = headView.findViewById(R.id.play_all_Layout);
        manger_layout = headView.findViewById(R.id.manger_layout);
        relayout_sel_cancel = headView.findViewById(R.id.relayout_sel_cancel);

        sel_all_Layout.setOnClickListener(this);
        cancel_layout.setOnClickListener(this);
        play_all_Layout.setOnClickListener(this);
        manger_layout.setOnClickListener(this);
        relayout_sel_cancel.setOnClickListener(this);



        rvList.setLayoutManager(new LinearLayoutManager(mFontext));

        ((SimpleItemAnimator)rvList.getItemAnimator()).setSupportsChangeAnimations(false);

        quickAdapter = new QuickAdapter(R.layout.item_sel_voice){
            @Override
            protected void convert(BaseViewHolder helper, Object item) {
                if(item instanceof ComAll) {
                    ComAll rankMore = (ComAll) item;

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

                        //  NLog.e(NLog.TAGDOWN," 视图 下载id : " + id);
                    }

//                    DownManger.updateViewHolder(id,new BaseDownViewHolder(id,helper.getLayoutPosition(),helper.getView(R.id.k_four),
//                            (TextView) helper.getView(R.id.item_down_tv)));

                    int statue = DownManger.isDownStatus(id, path);


                    if (statue == FileDownloadStatus.completed) {
                        helper.setGone(R.id.k_four, true);
                        helper.getView(R.id.k_four).setActivated(true);
                        helper.setText(R.id.item_down_tv,getString(R.string.已下载));

                    } else if (statue == FileDownloadStatus.progress||statue == FileDownloadStatus.started || statue == FileDownloadStatus.connected ) {
                        helper.getView(R.id.k_four).setActivated(false);
                        helper.setGone(R.id.k_four, true);
                        helper.setText(R.id.item_down_tv,getString(R.string.下载中));

                    } else {
                        helper.setGone(R.id.k_four, false);
                    }

                    helper.setGone(R.id.item_tv_num, true);
                    helper.setGone(R.id.item_tv_play_image, false);
                    //哪一个在播放
                    if (isPlay) {
                        if (comAll != null) {
                            if (rankMore.getId().equals(comAll.getId())) {
                                helper.setGone(R.id.item_tv_play_image, true);
                                helper.setGone(R.id.item_tv_num, false);
                            }
                        }
                    }

                    if (isSelMore) {

                        helper.setGone(R.id.sel_image, true);

                        if (issetSelList(rankMore)) {
                            helper.itemView.setSelected(true);
                        } else {
                            helper.itemView.setSelected(false);
                        }

                    } else {
                        helper.setGone(R.id.sel_image, false);
                    }

                    helper.setText(R.id.item_tv_num, getNumberIndex(helper.getLayoutPosition()));

                    helper.setText(R.id.item_ming_tv, rankMore.getTitle());

                    if (CheckUtil.isEmpty(rankMore.getBroad())) {
                        helper.setGone(R.id.k_one, false);
                    } else {
                        helper.setGone(R.id.k_one, true);
                        helper.setText(R.id.item_name_tv, rankMore.getBroad());
                    }
                    if (CheckUtil.isEmpty(rankMore.getPlaynums())) {
                        helper.setGone(R.id.k_two, false);
                    } else {
                        helper.setGone(R.id.k_two, true);
                        helper.setText(R.id.item_peo_tv, rankMore.getPlaynums());
                    }
                    helper.setText(R.id.item_time_tv, rankMore.getMinute() + ":" + rankMore.getSecond());

//
                    DownManger.setImageView((ImageView) helper.getView(R.id.head_portrait),
                            rankMore.getCover_small(),mFontext);
                }
            }
        };
        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Object item = quickAdapter.getItem(position);
                if(item instanceof  ComAll) {
                    ComAll rankMore = (ComAll) item;
                    if(isSelMore){
                        view.setSelected(!view.isSelected());
                        setSellList(rankMore);
                    }else {
                        CommonUtils.getIntent(mFontext, Arrays.asList((ComAll)item),0, GlobalVariable.FOUR);
                    }
                }

            }
        });
        quickAdapter.addHeaderView(headView);
        rvList.setAdapter(quickAdapter);
      //  DownManger.setQAdapter(quickAdapter);

    }

    @Override
    public void loadData() {
        super.loadData();
        upPlay();

        if(!CheckUtil.isEmpty(comAllList)){
            quickAdapter.replaceData(comAllList);
        }else {
          //  quickAdapter.setEmptyView(getEmpty("暂无记录"));
        }
        //getData();
    }


    @Override
    public void everyTime(boolean isVisibleToUser) {
        if(isGetData){
            isGetData = false;
          //  getData();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynEvent(Event.PlayEvent event) {

        int i = event.getI();

        if(i == 1){
            upPlay();
        }else if(i == 2){

        }else if(i == 3){
            isGetData = true;
        }

    }
    public void upPlay(){
        isPlay = true;
        if(isPlay){
            comAll = MusicPlayerManger.getCommAll();
        }
        if(null != quickAdapter)
            quickAdapter.notifyDataSetChanged();

    }
    private boolean issetSelList(ComAll comAll){

        for(int i = 0,j=selList.size();i<j;i++ ){
            if(comAll.getId().equals(selList.get(i).getId())){
                return true;
            }
        }

        return false;
    }
    private void setSellList(ComAll comAll){

        for(int i = 0,j=selList.size();i<j;i++ ){
            if(comAll.getId().equals(selList.get(i).getId())){
                selList.remove(i);
                sel_all_Layout.setSelected(false);
                if(CheckUtil.isEmpty(selList)){
                    buttomLayout.setVisibility(View.GONE);
                }
                return ;
            }
        }

        selList.add(comAll);

        if(selList.size() == comAllList.size()){
            sel_all_Layout.setSelected(true);
        }

        if(buttomLayout.getVisibility() == View.GONE){
            buttomLayout.setVisibility(View.VISIBLE);
        }

    }

    public void setQuickAdapter(List<ComAll> comAllList){
        this.comAllList = comAllList;
        if(!CheckUtil.isEmpty(comAllList)){
            if(null != quickAdapter){
                quickAdapter.replaceData(comAllList);
            }
        }else {
            if(null != quickAdapter){
                quickAdapter.replaceData(new ArrayList<>());
                quickAdapter.setEmptyView(getEmpty("暂无记录"));
            }
        }

    }

    private void delSelect(){
        deldelListFile(selList);

//        FlowManager.getDatabase(AppDatabase.class).beginTransactionAsync(new ITransaction() {
//            @Override
//            public void execute(DatabaseWrapper databaseWrapper) {
//                for(int i=0 , j = selList.size(); i<j; i++){
//                    ComAll comAll = selList.get(i);
//                    comAll.delete();
//                }
//            }
//        }).success(new Transaction.Success() {
//            @Override
//            public void onSuccess(@NonNull Transaction transaction) {
//                del_layout.setEnabled(true);
//                deldelListFile(selList);
//            }
//        }).error(new Transaction.Error() {
//            @Override
//            public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
//                del_layout.setEnabled(true);
//            }
//        }).build().execute();
    }

    private void deldelListFile(final List<ComAll> comAlls){
        EventBus.getDefault().post(new Event.UpDownEvent(0));

        DownManger.delListFile(comAlls, new DownManger.ResultCallback() {
            @Override
            public void onSuccess(Object o) {
                del_layout.setEnabled(true);
                NToast.shortToastBaseApp("删除成功");
                EventBus.getDefault().post(new Event.UpDownEvent(comAlls,1));
            }

            @Override
            public void onError(String errString) {

            }
        });
    }
}
