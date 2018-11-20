package fm.qian.michael.ui.fragment;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxbm.sbecomlibrary.utils.CheckUtil;
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
import java.util.List;

import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.db.AppDatabase;
import com.xxbm.sbecomlibrary.db.DownTasksModel;
import com.xxbm.sbecomlibrary.db.DownTasksModel_Table;
import com.xxbm.sbecomlibrary.db.TasksManagerModel;
import com.xxbm.sbecomlibrary.db.TasksManagerModel_Table;
import com.xxbm.sbecomlibrary.net.entry.response.Base;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.HeadGroupTopActivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NLog;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.widget.dialog.LoadingDialog;
import fm.qian.michael.widget.single.DownManger;

import static fm.qian.michael.utils.DisplayUtils.ImageHight3;

/*
 * lv   2018/11/6  下载专辑
 */
public class DownAlbumFragment extends BaseRecycleViewFragment {
    private LoadingDialog loadingDialog;
    private QuickAdapter quickAdapter;
    private boolean isGetData = false;
    List<DownTasksModel> downTasksModelList;
    @Override
    public boolean isDamp() {
        return true;
    }

    @Override
    public void initWidget(View view) {
        super.initWidget(view);

    }

    @Override
    public void init() {
        super.init();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        loadingDialog = new LoadingDialog(mFontext);
        getRvList().setLayoutManager(new LinearLayoutManager(mFontext));
        ((SimpleItemAnimator)getRvList().getItemAnimator()).setSupportsChangeAnimations(false);
        quickAdapter =  new QuickAdapter(R.layout.item_cst_swipe){
            @Override
            protected void convert(final BaseViewHolder helper, Object item) {

                LayoutParmsUtils.setHight(ImageHight3(),helper.getView(R.id.item_image));

                if(item instanceof DownTasksModel){
                    final DownTasksModel comAll = (DownTasksModel) item;

                    if(comAll.getComAlls() != null){
                        helper.setText(R.id.item_tv,"共"+comAll.getSizeAll()+"个下载" + comAll.getComAlls().size());
                    }else {
                        helper.setText(R.id.item_tv,"共"+comAll.getSizeAll()+"个");
                    }

                    DownManger.setImageView((ImageView) helper.getView(R.id.item_image),
                            comAll.getCover(),mFontext);

                    helper.setOnClickListener(R.id.btnDelete, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            delData(comAll,helper.getLayoutPosition());

                        }
                    });

                    helper.setOnClickListener(R.id.content, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(mFontext,HeadGroupActivity.class);
                            intent.putExtra("DownTasksModel",comAll);
                            startActivity(intent);

                        }
                    });

                }else {
                    helper.setGone(R.id.item_tv,false);
                }

            }
        };
        View headView = LayoutInflater.from(mFontext).inflate(R.layout.item_head_tbgmjl_layout,null,false);

        TextView textView = headView.findViewById(R.id.tv_tsy);
        textView.setText("左滑封面可以删除已下载专辑");

        quickAdapter.addHeaderView(headView);

        getRvList().setAdapter(quickAdapter);


    }

    @Override
    public void loadData() {
        super.loadData();

        if(!CheckUtil.isEmpty(downTasksModelList)){
            if(CheckUtil.isEmpty(quickAdapter.getData()))
            quickAdapter.replaceData(downTasksModelList);
        }else {
           // quickAdapter.setEmptyView(getEmpty("暂无记录"));
        }

    }
    @Override
    public void everyTime(boolean isVisibleToUser) {
        if(isGetData){
            isGetData = false;
        }
    }
    @Subscribe(threadMode = ThreadMode.POSTING) //在ui线程执行
    public void onDataSynEvent(Event.PlayEvent event) {

        int i = event.getI();

        if(i == 1){

        }else if(i == 2){

        }else if(i == 4){
            isGetData = true;
        }

    }

    public void setQuickAdapter(List<DownTasksModel> comAlls){
        downTasksModelList = comAlls;
        if(!CheckUtil.isEmpty(downTasksModelList)){
            if(null != quickAdapter){
                quickAdapter.replaceData(downTasksModelList);
            }
        }else {
            if(null != quickAdapter){
                quickAdapter.replaceData(new ArrayList<>());
                quickAdapter.setEmptyView(getEmpty("暂无记录"));
            }
        }


    }

    private void getBenDIList(){

    }

    private List<ComAll> list;
    private void delData(final DownTasksModel comAll, final int point){
        list = comAll.getComAlls();
//        comAll.async().success(new Transaction.Success() {
//            @Override
//            public void onSuccess(@NonNull Transaction transaction) {
//
//            }
//        }).delete();
        quickAdapter.remove(point-1);
        if(CheckUtil.isEmpty(quickAdapter.getData())){
           // quickAdapter.setEmptyView(getEmpty("暂无记录"));
        }
        deldelListFile(list);
    }
    private void deldelListFile(final List<ComAll> comAlls){
        EventBus.getDefault().post(new Event.UpDownEvent(0));
        DownManger.delListFile(comAlls, new DownManger.ResultCallback() {
            @Override
            public void onSuccess(Object o) {
                NToast.shortToastBaseApp("删除成功");

                EventBus.getDefault().post(new Event.UpDownEvent(comAlls,1));

            }

            @Override
            public void onError(String errString) {

            }
        });
    }
}
