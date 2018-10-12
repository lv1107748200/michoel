package fm.qian.michael.ui.activity.dim;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.hr.bclibrary.utils.CheckUtil;
import com.king.zxing.Intents;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;


import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.base.activity.BaseRecycleViewActivity;
import fm.qian.michael.base.activity.PermissionCallback;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.http.HttpCallback;
import fm.qian.michael.net.http.HttpException;
import fm.qian.michael.ui.activity.CustomCaptureActivity;
import fm.qian.michael.ui.adapter.PoliticalNewsAdater;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.ui.adapter.SearchFragmentAdater;
import fm.qian.michael.ui.fragment.ArticleFragment;
import fm.qian.michael.ui.fragment.SearchFragment;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.utils.NToast;
import fm.qian.michael.widget.custom.SearchLayout;
import fm.qian.michael.widget.custom.XCViewPager;

/**
 * Created by 吕 on 2018/1/8.
 */

public class SearchActivity extends BaseIntensifyActivity implements SearchLayout.SearchCallBack{

    public static final int REQUEST_CODE_SCAN = 0X01;
    private boolean isMake = true;

    private int p = -1;
    private int p1 = -1;

    private int hight = -1;

    @BindView(R.id.status_bar)
    View statusBar;
    @BindView(R.id.id_search)
    SearchLayout idSearch;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.xViewPager_main)
    XCViewPager xViewPagerMain;
    @BindView(R.id.search_main_layout)
    LinearLayout search_main_layout;

    private SearchFragmentAdater searchFragmentAdater;


    private String t;


    @OnClick({R.id.base_left_layout})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.base_left_layout:
                finish();
                break;
        }
    }

    private String kind;
    private String searchText = "";
    @Override
    public int getLayout() {
        return R.layout.activity_search;
    }

    @Override
    public void initTitle() {
        super.initTitle();
    }

    @Override
    public void initView() {
        super.initView();

        isMake = true;

        setTitleTv(getString(R.string.自选));

        setStatusBar(statusBar);
        idSearch.setSearchCallBack(this);
        idSearch.setNv(View.GONE);
        idSearch.setImageScan(View.VISIBLE);//显示扫描按钮
        idSearch.setSearch_et_inputHint("请输入关键字");//显示扫描按钮

        kind = getIntent().getStringExtra("kind");

        final   int[] id = {R.string.专辑,R.string.故事,R.string.视频};

        List list = Arrays.asList(new String[]{"","",""});

        xViewPagerMain.setScrollable(true);
        searchFragmentAdater = new SearchFragmentAdater(getSupportFragmentManager());
        xViewPagerMain.setAdapter(searchFragmentAdater);


        List<SearchFragment> searchFragments = new ArrayList<>();

        for(int i=0; i<list.size(); i++){
            searchFragments.add(SearchFragment.getSearchFragment().setType(i+1));
        }

        searchFragmentAdater.setTitles(searchFragments);

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.25f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return id.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(id[index]);
                simplePagerTitleView.setNormalColor(Color.parseColor("#292B2F"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#F86E78"));
                simplePagerTitleView.setTextSize(15);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        xViewPagerMain.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setColors(Color.parseColor("#F86E78"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);


        xViewPagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
            }
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);

                if(state == 2){
                    searchFragmentAdater.getItem(xViewPagerMain.getCurrentItem()).set(searchText);
                    searchFragmentAdater.getItem(xViewPagerMain.getCurrentItem()).load();
                }

            }
        });

        xViewPagerMain.setCurrentItem(1,false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
            if(isMake){
                isMake = false;
                searchFragmentAdater.getItem(xViewPagerMain.getCurrentItem()).set(searchText);
                searchFragmentAdater.getItem(xViewPagerMain.getCurrentItem()).load();
            }
    }

    @Override
    public void initData() {
        super.initData();
        search_hotwords();
    }

    @Override
    public void textCallBack(String text) {
        idSearch.setNv(View.GONE);
        search_main_layout.setVisibility(View.VISIBLE);
        searchText = text;

        searchFragmentAdater.getItem(xViewPagerMain.getCurrentItem()).set(searchText);
        searchFragmentAdater.getItem(xViewPagerMain.getCurrentItem()).load();
    }

    @Override
    public void show(int i) {
        search_main_layout.setVisibility(i);
    }

    @Override
    public void del() {
        idSearch.setNv(View.GONE);
        search_main_layout.setVisibility(View.VISIBLE);
        searchText = "";

        searchFragmentAdater.getItem(xViewPagerMain.getCurrentItem()).set(searchText);
        searchFragmentAdater.getItem(xViewPagerMain.getCurrentItem()).load();
    }

    @Override
    public void scan() {

        requestPermission(Manifest.permission.CAMERA, new PermissionCallback() {
            @Override
            public void requestP(boolean own) {
                if(own){
                    startScan();
                }else {
                    NToast.shortToastBaseApp("扫码请开权限");
                }
            }
        });

    }

    private void search_hotwords(){

        baseService.search_hotwords("1", new HttpCallback<List<Base>, BaseDataResponse<List<Base>>>() {
            @Override
            public void onError(HttpException e) {

            }

            @Override
            public void onSuccess(List<Base> baseList) {

                if(!CheckUtil.isEmpty(baseList)){
                    idSearch.setId_flowlayout(baseList);
                }else {

                }
            }
        },SearchActivity.this.bindUntilEvent(ActivityEvent.DESTROY));

    }


    private void startScan(){
        Intent intent = new Intent(this, CustomCaptureActivity.class);
        startActivityForResult(intent,REQUEST_CODE_SCAN);
    }

    private void isbn(String isbn){
        baseService.isbn(isbn, new HttpCallback<ComAll, BaseDataResponse<ComAll>>() {
            @Override
            public void onError(HttpException e) {
                NToast.shortToastBaseApp(e.getMsg());
            }

            @Override
            public void onSuccess(ComAll comAll) {

                Intent intent = new Intent();

                Base base = new Base();

                base.setTid(comAll.getTid());
                base.setZid(comAll.getZid());

                CommonUtils.setIntent(intent,SearchActivity.this,base);

            }
        },SearchActivity.this.bindUntilEvent(ActivityEvent.DESTROY));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case REQUEST_CODE_SCAN:
                    String result = data.getStringExtra(Intents.Scan.RESULT);

                    if(!CheckUtil.isEmpty(result)){
                        isbn(result);
                    }

                   // Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

}
