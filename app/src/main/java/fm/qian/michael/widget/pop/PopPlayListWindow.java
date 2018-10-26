package fm.qian.michael.widget.pop;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hr.bclibrary.utils.CheckUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.base.fragment.BaseFragment;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.base.BaseService;
import fm.qian.michael.net.entry.Video;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.UserInfo;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.adapter.QuickAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.ui.fragment.GroupVoiseFragment;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.single.UserInfoManger;
import io.reactivex.ObservableTransformer;

/*
 * lv   2018/9/11
 */
public class PopPlayListWindow extends BasePopupWindow {

    private QuickAdapter quickAdapter;

    private PopPlayListWindowCallBack popPlayListWindowCallBack;

    public void setPopPlayListWindowCallBack(PopPlayListWindowCallBack popPlayListWindowCallBack) {
        this.popPlayListWindowCallBack = popPlayListWindowCallBack;
    }

    @BindView(R.id.play_list_recycle)
    RecyclerView playListRecycle;
    @BindView(R.id.main_search)
    EditText main_search;

    @OnClick({R.id.layoutQX, R.id.layoutQD})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.layoutQX://取消
                dismiss();
                break;
            case R.id.layoutQD:

                if(null != popPlayListWindowCallBack){

                    String name = main_search.getText().toString();
                    if(CheckUtil.isEmpty(name))
                    {
                        NToast.shortToastBaseApp("请输入播单名称");
                        return;
                    }

                    setUserInfo("add",name,null, CommonUtils.setJoint(popPlayListWindowCallBack.getSelComAll()));//创建并添加播单

                }

                break;
        }
    }

    private BaseFragment contextF;
    private BaseActivity contextA;

    public PopPlayListWindow(BaseFragment context,CustomPopuWindConfig config) {
        super(config);
        this.contextF = context;
        initView();
    }

    public PopPlayListWindow(BaseActivity context,CustomPopuWindConfig config) {
        super(config);
        this.contextA = context;
        initView();
    }


    @Override
    public View getView(Context context) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.pop_add_play_list,
                null,false
        );

        ButterKnife.bind(this,view);

        return view;
    }


    private void initView(){

        playListRecycle.setLayoutManager(new LinearLayoutManager(getContext()));

        quickAdapter = new QuickAdapter(R.layout.item_pop_play_list){
            @Override
            protected void convert(BaseViewHolder helper, Object item) {

                helper.setGone(R.id.view_line,false);

                if(item instanceof ComAll){
                    ComAll comAll = (ComAll)item;

                    helper.setText(R.id.what_tv,comAll.getTitle());

                }

            }
        };

        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(null != popPlayListWindowCallBack){
                    setUserInfo("add",null,((ComAll) quickAdapter.getItem(position)).getId(),
                            CommonUtils.setJoint(popPlayListWindowCallBack.getSelComAll()));//添加播单
                }
            }
        });

        playListRecycle.setAdapter(quickAdapter);

    }

    public void updataPlayListRecycle(List list){
        if(null != quickAdapter){
            quickAdapter.replaceData(list);
        }
    }

    @Override
    public void show(View parent) {
        super.show(parent);

        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, Gravity.CENTER,0, 0);

        } else {
            this.dismiss();
        }
    }

    @Override
    public void onDiss() {

    }


    public interface PopPlayListWindowCallBack{
        List<ComAll> getSelComAll();
        void state(int what);
    }



    private void setUserInfo(String act,String title,String bid,String mids){

        UserInfo data = new UserInfo();
        data.setAct(act);
        data.setUsername(UserInfoManger.getInstance().getUserName());
        data.setSessionkey(UserInfoManger.getInstance().getSessionkey());
        if(null != title)
            data.setTitle(title);
        if(null != mids)
            data.setMids(mids);
        if(null != bid)
            data.setBid(bid);

        user_broadcastlist(data);

    }

    //加入播单
    private void user_broadcastlist(UserInfo data){
        getBaseService().user_broadcastlist(data, new HttpCallback<Object, BaseDataResponse<Object>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(Object comAll) {
                NToast.shortToastBaseApp("已添加至播单，请前往我的播单查看");

                if(null != popPlayListWindowCallBack){
                    popPlayListWindowCallBack.state(0);
                }

                dismiss();

                EventBus.getDefault().post(new Event.LoginEvent(GlobalVariable.ONE));

            }
        }.setContext(getContext()), getObservableTransformer());
    }


    public void user_broadcastall(){

        UserInfo data = new UserInfo();
        data.setAct("listadd");
        data.setUsername(UserInfoManger.getInstance().getUserName());
        data.setSessionkey(UserInfoManger.getInstance().getSessionkey());


        getBaseService().user_broadcastall(
                data,
                new HttpCallback<List<ComAll>, BaseDataResponse<List<ComAll>>>() {
                    @Override
                    public void onError(HttpException e) {
                        NToast.shortToastBaseApp(e.getMsg());
                    }

                    @Override
                    public void onSuccess(List<ComAll> comAlls) {

                        updataPlayListRecycle(comAlls);

                    }
                }.setContext(getContext()), getObservableTransformer()

        );

    }

    private Context getContext(){
        if(null != contextF){
            return contextF.getContext();
        }else if(null != contextA){
            return contextA;
        }

        return null;
    }

    private BaseService getBaseService(){
        if(null != contextF){
            return contextF.baseService;
        }else if(null != contextA){
            return contextA.baseService;
        }

        return null;
    }

    private ObservableTransformer getObservableTransformer(){
        if(null != contextF){
            return contextF.bindUntilEvent(FragmentEvent.DESTROY_VIEW);
        }else if(null != contextA){
            return contextA.bindUntilEvent(ActivityEvent.DESTROY);
        }
        return null;
    }

}
