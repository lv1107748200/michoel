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
import com.hr.bclibrary.utils.CheckUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.base.activity.BaseRecycleViewActivity;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.net.base.BaseResponse;
import fm.qian.michael.net.entry.Video;
import fm.qian.michael.net.entry.response.Album;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.service.MqService;
import fm.qian.michael.ui.activity.MainActivity;
import fm.qian.michael.ui.activity.WebParticularsActivity;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.ui.fragment.GroupVoiseFragment;
import fm.qian.michael.ui.fragment.HeadGroupFragment;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.custom.SelectableRoundedImageView;

import butterknife.BindView;
import butterknife.OnClick;

import static fm.qian.michael.service.MqService.CMDNOTIF;


/*
 * lv   2018/9/8  在此处理专辑  显示页面
 */
public class HeadGroupActivity extends BaseIntensifyActivity {
    public static final String HEADGROUP = "HEADGROUP";
    public static final int ONE = 1;//音频专辑
    public static final int TWO = 2;//视频集合

    private String id;

    private GroupVoiseFragment   groupVoiseFragment;
    @OnClick({R.id.base_left_layout, R.id.base_right_layout})
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.base_left_layout:
                finish();
                break;
            case R.id.base_right_layout:

                break;
        }
    }

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
    public void initData() {
        super.initData();

        Intent intent = getIntent();

        id = intent.getStringExtra(HEADGROUP);

        if(!CheckUtil.isEmpty(id)){
            album();
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

                       groupVoiseFragment = new GroupVoiseFragment();
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
