package fm.qian.michael.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hr.bclibrary.utils.CheckUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle2.android.FragmentEvent;
import fm.qian.michael.R;
import fm.qian.michael.base.fragment.BaseFragment;
import fm.qian.michael.base.fragment.BaseRecycleScFragment;
import fm.qian.michael.base.fragment.BaseRecycleViewFragment;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.DataServer;
import fm.qian.michael.net.entry.MySection;
import fm.qian.michael.net.entry.Video;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.Category;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.adapter.SectionAdapter;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.ItemDecoration.RecyclerViewSpacesItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.net.entry.DataServer;
import fm.qian.michael.net.entry.MySection;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.adapter.SectionAdapter;

import static fm.qian.michael.ui.activity.dim.HeadGroupActivity.HEADGROUP;

/**
 * Created by 吕 on 2017/12/6.
 * 分类
 */

public class CategoryFragment extends BaseRecycleViewFragment {


    private List<MySection> mData;
    private SectionAdapter sectionAdapter;

    private boolean isError = false;


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

//        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
//
//        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION,
//                DisplayUtils.getDimen(R.dimen.margin_10));//底部间距
//
//        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION,
//                DisplayUtils.getDimen(R.dimen.margin_10));//右间距
//        //rvList.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));


        getRvList().setLayoutManager(new GridLayoutManager(getActivity(),3));

        mData = DataServer.getSampleData();
        sectionAdapter = new SectionAdapter(R.layout.item_image_and_text, R.layout.item_head_one, mData);

        sectionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();

                MySection mySection = sectionAdapter.getData().get(position);

                if(mySection.t instanceof Base)
                 CommonUtils.setIntent(intent,mFontext, (Base) mySection.t);

            }
        });


        getRvList().setAdapter(sectionAdapter);
    }

    @Override
    public void loadData() {
        super.loadData();
        category();
    }

    @Override
    public void everyTime(boolean isVisibleToUser) {
        if(isVisibleToUser){
            if(isError){
                isError = false;
                category();
            }
        }
    }

    @Override
    public boolean isDamp() {
        return true;
    }

    private void category(){
        baseService.category(new HttpCallback<List<Category>, BaseDataResponse<List<Category>>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());

                isError = true;
            }

            @Override
            public void onSuccess(List<Category> categories) {
                List<MySection> list = new ArrayList<>();
                for(int i =0,j=categories.size(); i< j; i++){
                    Category category = categories.get(i);

                    list.add(new MySection(true, category.getName(), false));

                    List<Base> bases = category.getList();
                    if(!CheckUtil.isEmpty(bases)){
                        for(int k = 0, h = bases.size();k<h;k++){
                            list.add(new MySection(bases.get(k)));
                        }
                    }
                }
                sectionAdapter.replaceData(list);

            }
        },CategoryFragment.this.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }

}
