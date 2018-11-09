package fm.qian.michael.ui.activity.dim;


import android.content.Context;
import android.graphics.Color;
import android.view.View;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.base.activity.BaseRecycleViewActivity;
import fm.qian.michael.ui.adapter.PoliticalNewsAdater;
import fm.qian.michael.ui.adapter.QuickAdapter;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.widget.custom.XCViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.base.activity.BaseIntensifyActivity;
import fm.qian.michael.utils.NLog;

/*
 * lv   2018/9/11
 */
public class TopListActivity extends BaseIntensifyActivity {

    public static final String TOPLIST = "TOPLIST";
    public static final int ONE = 1;//故事排行
    public static final int TWO = 2;//专辑排行

    private PoliticalNewsAdater politicalNewsAdater;

    @BindView(R.id.magic_indicator)
    MagicIndicator  magicIndicator;
    @BindView(R.id.xViewPager_main)
    XCViewPager xViewPagerMain;

    @Override
    public int getLayout() {
        return  R.layout.activity_top_list;
    }

    @Override
    public void initView() {
        super.initView();
        setTitleTv(getString(R.string.榜单));

        final   int[] id = {fm.qian.michael.R.string.本周故事, fm.qian.michael.R.string.本周专辑, fm.qian.michael.R.string.本月故事, fm.qian.michael.R.string.本月专辑};

        List list = Arrays.asList(new String[]{"","","",""});

        xViewPagerMain.setScrollable(true);
        politicalNewsAdater = new PoliticalNewsAdater(getSupportFragmentManager());
        xViewPagerMain.setAdapter(politicalNewsAdater);
        politicalNewsAdater.setTitles(list);

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

                        NLog.e(NLog.TAGOther,"点击编号 --->" + index);

                        xViewPagerMain.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setXOffset(DisplayUtils.getDimen(R.dimen.margin_10));
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setColors(Color.parseColor("#F86E78"));
                indicator.setRoundRadius(DisplayUtils.getDimen(R.dimen.radius));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, xViewPagerMain);


        xViewPagerMain.setCurrentItem(0,false);
    }

    @Override
    public void initData() {
        super.initData();

    }
}
