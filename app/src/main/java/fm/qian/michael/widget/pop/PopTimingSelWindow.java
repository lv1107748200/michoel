package fm.qian.michael.widget.pop;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import fm.qian.michael.R;
import fm.qian.michael.net.entry.Video;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import com.xxbm.musiclibrary.MusicPlayerManger;
import fm.qian.michael.ui.adapter.QuickAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.xxbm.sbecomlibrary.utils.NToast;

/*
 * lv   2018/9/11
 */
public class PopTimingSelWindow extends BasePopupWindow {

    private int type;

    private QuickAdapter quickAdapter;
    private Context context;
    private PopTimingSelCallBack popTimingSelCallBack;
    private int sel = 0;
    private String id;

    @BindView(R.id.timing_rec)
    RecyclerView timingRec;

    @OnClick({R.id.close_layout})
    public  void  OnClick(View view){
        switch (view.getId()){
            case R.id.close_layout:
                if(isShowing()){
                    dismiss();
                }
                break;
        }
    }
    public PopTimingSelWindow(Context context, CustomPopuWindConfig config) {
        super(config);
        this.context = context;
        initView();
    }

    public void setPopTimingSelCallBack(PopTimingSelCallBack popTimingSelCallBack) {
        this.popTimingSelCallBack = popTimingSelCallBack;
    }

    @Override
    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_timing_sel,
                null,false);
        ButterKnife.bind(this,view);
        return view;
    }

    private void initView(){
        timingRec .setLayoutManager(new LinearLayoutManager(context));
        quickAdapter = new QuickAdapter(R.layout.item_pop_play_list){
            @Override
            protected void convert(BaseViewHolder helper, Object item) {

                TextView textView = helper.getView(R.id.what_tv);

                textView.setPadding(0,0,0,0);

                if(item instanceof String){
                    if(sel == helper.getLayoutPosition()){
                        helper.itemView.setSelected(true);
                    }else {
                        helper.itemView.setSelected(false);
                    }

                    textView.setText((String)item);
                }else if(item instanceof ComAll){

                    if(((ComAll) item).getId().equals(id)){
                        helper.itemView.setSelected(true);
                    }else {
                        helper.itemView.setSelected(false);
                    }

                    textView.setText(((ComAll) item).getTitle());

                }

            }
        };

        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Object item = quickAdapter.getItem(position);

                if(null != popTimingSelCallBack){
                    if(item instanceof String){
                        popTimingSelCallBack.callBackNum(position);
                        setSel(position);
                    }else if(item instanceof ComAll){
                        if(((ComAll) item).getId().equals(id)){
                            NToast.shortToastBaseApp("正在播放");
                        }else {
                            popTimingSelCallBack.callBackComAll((ComAll) item,position);
                        }

                       // setComAllTwo( ((ComAll) item).getId());
                    }
                }


            }
        });

        timingRec.setAdapter(quickAdapter);
    }



    public void setQuickAdapter(List list,int type){
        this.type = type;
        if(type == 0){
            List<String> stringList = Arrays.asList(new String[]{"不开启"
                    ,"播完当前声音","10分钟后","20分钟后","30分钟后",
                    "40分钟后","60分钟后","90分钟后"});

            quickAdapter.replaceData(stringList);
        }else if(type == 1){
            quickAdapter.replaceData(list);
        }
    }

    public int getType() {
       return this.type;
    }

    //处理定时任务
    public void setSel(int sel) {
        this.sel = sel;
        if(null != quickAdapter){
            quickAdapter.notifyDataSetChanged();
        }
    }
    //处理播放列表
    public void setComAll(String id) {
        if(null == id)
            return;
        this.id = id;
        if(null != quickAdapter){
            quickAdapter.notifyDataSetChanged();
        }

        int position =   MusicPlayerManger.getPlayNumber();
        if (position != -1) {
            timingRec.scrollToPosition(position);
            LinearLayoutManager mLayoutManager =
                    (LinearLayoutManager) timingRec.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
        }

    }
    //处理播放列表
    public void setComAllTwo(String id) {
        if(null == id)
            return;
        this.id = id;
        if(null != quickAdapter){
            quickAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void show(View parent) {
        super.show(parent);

        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, Gravity.BOTTOM,0, 0);

        } else {
            this.dismiss();
        }
    }

    @Override
    public void onDiss() {

    }

    public interface PopTimingSelCallBack extends PopCallBack{
        void callBackNum(int num);//定时任务
        void callBackComAll(ComAll comAll,int num);//选集
    }
}
