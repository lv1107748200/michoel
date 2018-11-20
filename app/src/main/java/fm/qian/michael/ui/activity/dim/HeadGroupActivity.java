package fm.qian.michael.ui.activity.dim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xxbm.sbecomlibrary.utils.CheckUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.base.activity.BaseRecycleViewActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.common.event.Event;
import com.xxbm.sbecomlibrary.db.DownTasksModel;
import com.xxbm.sbecomlibrary.net.base.BaseResponse;
import fm.qian.michael.net.entry.Video;
import com.xxbm.sbecomlibrary.net.entry.response.Album;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;
import com.xxbm.sbecomlibrary.net.http.HttpCallback;
import com.xxbm.sbecomlibrary.net.http.HttpException;
import com.xxbm.musiclibrary.MqService;
import fm.qian.michael.ui.activity.MainActivity;
import fm.qian.michael.ui.activity.WebParticularsActivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.ui.fragment.GroupVoiseFragment;
import fm.qian.michael.ui.fragment.HeadGroupFragment;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import com.xxbm.sbecomlibrary.utils.NToast;
import fm.qian.michael.widget.custom.SelectableRoundedImageView;

import butterknife.BindView;
import butterknife.OnClick;

import static com.xxbm.musiclibrary.MqService.CMDNOTIF;


/*
 * lv   2018/9/8  在此处理专辑  显示页面
 */
public class HeadGroupActivity extends BaseIntensifyActivity {
    public static final String HEADGROUP = "HEADGROUP";
    public static final int ONE = 1;//音频专辑
    public static final int TWO = 2;//视频集合

    private String id;
    private DownTasksModel downTasksModel;


    @Override
    public int getLayout() {
        return R.layout.activity_head_group;
    }

    @Override
    public void initView() {
        super.initView();
        setTitleTv("专辑");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        if(null != intent)
        initData(intent);
    }
    @Override
    public void initData(Intent intent) {
        super.initData();
        id = intent.getStringExtra(HEADGROUP);
        downTasksModel = intent.getParcelableExtra("DownTasksModel");
        if(!CheckUtil.isEmpty(id)){
            album();
        }else if(!CheckUtil.isEmpty(downTasksModel)){
            setTitleTv(downTasksModel.getTitle());
            GroupVoiseFragment   groupVoiseFragment = new GroupVoiseFragment();
            groupVoiseFragment.setK(downTasksModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, groupVoiseFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void  album(){
        baseService.album(id, 1 + "", "", "", new HttpCallback<Album, BaseResponse<Album, List<ComAll>>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccessAll(BaseResponse<Album, List<ComAll>> k) {
                Album album = k.getInfo();


                setTitleTv(album.getTitle());

                if(GlobalVariable.ONE.equals(album.getTid())){//音频专辑处理

                    GroupVoiseFragment   groupVoiseFragment = new GroupVoiseFragment();
                    groupVoiseFragment.setK(k);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, groupVoiseFragment).commit();


                }else {//其他专辑处理

                    HeadGroupFragment headGroupFragment = new HeadGroupFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, headGroupFragment).commit();

                }


            }
        },this.bindUntilEvent(ActivityEvent.DESTROY));
    }

}
