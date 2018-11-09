package fm.qian.michael.ui.fragment;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;
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
import fm.qian.michael.db.DownTasksModel;
import fm.qian.michael.db.DownTasksModel_Table;
import fm.qian.michael.db.TasksManagerModel;
import fm.qian.michael.db.TasksManagerModel_Table;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.HeadGroupTopActivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
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

                    if(comAll.comAlls != null){
                        helper.setText(R.id.item_tv,"共"+comAll.getSizeAll()+"个下载" + comAll.comAlls.size());
                    }else {
                        helper.setText(R.id.item_tv,"共"+comAll.getSizeAll()+"个");
                    }
                    GlideUtil.setGlideImageMake(mFontext,comAll.getCover(),
                            (ImageView) helper.getView(R.id.item_image));

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
//        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                Object item = quickAdapter.getItem(position);
//
//                if(item instanceof DownTasksModel){
//                    Intent intent = new Intent(mFontext,HeadGroupActivity.class);
//                    intent.putExtra("DownTasksModel",(DownTasksModel)item);
//                    startActivity(intent);
//                }
//            }
//        });

        getRvList().setAdapter(quickAdapter);


    }

    @Override
    public void loadData() {
        super.loadData();
        getBenDIList();
    }
    @Override
    public void everyTime(boolean isVisibleToUser) {
        if(isGetData){
            isGetData = false;
            getBenDIList();
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
    //
    private void getBenDIList(){
        loadingDialog.show("加载数据");
        SQLite.select()
                .from(DownTasksModel.class)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<DownTasksModel>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<DownTasksModel> tResult) {
                        NLog.e(NLog.TAGDOWN, "下载专辑   SQLite  onListQueryResult 查询成功");
                        if(!CheckUtil.isEmpty(tResult)){
                            NLog.e(NLog.TAGDOWN, "下载专辑   SQLite  onListQueryResult 查询成功: " + tResult.size());
//                            DownTasksModel downTasksModel = tResult.get(0);
//
//                            NLog.e(NLog.TAGDOWN, "下载专辑  任务: " + downTasksModel.comAlls.size());

                            quickAdapter.replaceData(tResult);

                        }else {
                            //NToast.shortToastBaseApp("暂无记录");

                            quickAdapter.replaceData(new ArrayList<>());
                            quickAdapter.setEmptyView(getEmpty("暂无记录"));
                        }
                        loadingDialog.diss();
                    }
                })
                .error(new Transaction.Error() {
                    @Override
                    public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
                        NLog.e(NLog.TAGDOWN, "下载专辑   SQLite  error"+error.getMessage());
                        loadingDialog.diss();
                    }
                })
                .execute();
    }

    private List<ComAll> list;
    private void delData(final DownTasksModel comAll, final int point){
        list = comAll.comAlls;
        comAll.async().success(new Transaction.Success() {
            @Override
            public void onSuccess(@NonNull Transaction transaction) {
                quickAdapter.remove(point);
                if(CheckUtil.isEmpty(quickAdapter.getData())){
                    quickAdapter.setEmptyView(getEmpty("暂无记录"));
                }
                EventBus.getDefault().post(new Event.PlayEvent(3));
                deldelListFile(list);
            }
        }).delete();

    }
    private void deldelListFile(List<ComAll> comAlls){
        DownManger.delListFile(comAlls, new DownManger.ResultCallback() {
            @Override
            public void onSuccess(Object o) {
                NToast.shortToastBaseApp("删除成功");
            }

            @Override
            public void onError(String errString) {

            }
        });
    }
}
