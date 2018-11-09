package fm.qian.michael.ui.activity.dim;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.ui.adapter.DownFragmentAdater;
import fm.qian.michael.ui.fragment.DownAlbumFragment;
import fm.qian.michael.ui.fragment.DownStoryFragment;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.widget.custom.DownTabLayout;
import fm.qian.michael.widget.custom.XCViewPager;
import fm.qian.michael.widget.swipemenulib.CstViewPager;

/*
 * lv   2018/11/6
 */
public class DownActivity extends BaseIntensifyActivity {

    private DownFragmentAdater downFragmentAdater;

    @BindView(R.id.magic_indicator)
    DownTabLayout magicIndicator;
    @BindView(R.id.xViewPager_main)
    CstViewPager xViewPagerMain;



    @Override
    public int getLayout() {
        return R.layout.activity_down;
    }

    @Override
    public void initView() {
        super.initView();
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
