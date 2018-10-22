package fm.qian.michael.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import fm.qian.michael.R;
import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.net.entry.MultipleItem;
import fm.qian.michael.net.entry.Video;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.net.entry.response.ComAll;
import fm.qian.michael.net.entry.response.Rank;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.ui.activity.ComActivity;
import fm.qian.michael.ui.activity.WebParticularsActivity;
import fm.qian.michael.ui.activity.dim.HeadGroupActivity;
import fm.qian.michael.ui.activity.dim.PlayActivity;
import fm.qian.michael.ui.activity.dim.TopListActivity;
import fm.qian.michael.utils.CommonUtils;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.widget.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fm.qian.michael.net.entry.MultipleItem;
import fm.qian.michael.net.entry.Video;
import fm.qian.michael.ui.activity.dim.TopListActivity;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.LayoutParmsUtils;
import fm.qian.michael.utils.NLog;
import fm.qian.michael.widget.GlideImageLoader;

import static fm.qian.michael.ui.activity.dim.TopListActivity.TOPLIST;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 * modify by AllenCoder
 */
public class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder> {

    private Context context;

    public MultipleItemQuickAdapter(Context context, List data) {
        super(data);
        this.context = context;
        addItemType(MultipleItem.BAN, R.layout.item_find_ban);
        addItemType(MultipleItem.HEAD, R.layout.item_head_one);
        addItemType(MultipleItem.IMGANDTEXT, R.layout.item_image_and_text);
        addItemType(MultipleItem.IMGANDTEXTG2, R.layout.item_image_and_text_changed_one);
        addItemType(MultipleItem.IMGANDTEXTG4, R.layout.item_image_and_text_set_sp);
        addItemType(MultipleItem.TEXTDATE, R.layout.item_text_layout);
        addItemType(MultipleItem.RANKING, R.layout.item_ranking);
        addItemType(MultipleItem.RECY, R.layout.item_recycleview);
    }

    @Override
    protected void convert(BaseViewHolder helper, final MultipleItem item) {
        switch (helper.getItemViewType()) {
            case MultipleItem.BAN://轮播图
                Banner banner1 = helper.itemView.findViewById(fm.qian.michael.R.id.banner);

                if(item.getObject() instanceof List){
                    final List list = (List) item.getObject();
                    banner1.setImages(list)
                            .setDelayTime(3000)
                            .setImageLoader(new GlideImageLoader())
                            .setOnBannerListener(new OnBannerListener() {
                                @Override
                                public void OnBannerClick(int position) {
                                    NLog.e(NLog.TAGOther,"轮播--->" + position);
                                    if(list.get(position) instanceof Base){

                                        Base base = (Base) list.get(position);
                                        Intent intent = new Intent();

                                        CommonUtils.setIntent(intent,context,base);
                                    }
                                }
                            })
                            .start();
                }


                break;
            case MultipleItem.HEAD:// 头部
                int p2 = DisplayUtils.getDimen(R.dimen.item_margin_six);
                int p3 = DisplayUtils.getDimen(R.dimen.item_margin_two);

                helper.itemView.setPadding(p3,p2,p3,p2);

                if(item.getObject() instanceof Video){
                    Video video = (Video) item.getObject();

                    helper.setText(R.id.head_tv,video.getName());

                    if(video.getId() == -1){
                        helper.setVisible(R.id.head_tv_message,false);
                        helper.setVisible(R.id.head_image,false);

                    }else {
                        helper.setVisible(R.id.head_image,true);
                        if(video.getId() == R.drawable.myjt1){
                            helper.setBackgroundRes(R.id.head_image, video.getId());
                            helper.setVisible(R.id.head_tv_message,true);
//                            LayoutParmsUtils.setView(DisplayUtils.getDimen(R.dimen.margin_14),
//                                    helper.getView(R.id.item_image));
                        }else {
                            helper.setBackgroundRes(R.id.head_image, video.getId());
                            helper.setVisible(R.id.head_tv_message,false);
//                            LayoutParmsUtils.setView(DisplayUtils.getDimen(R.dimen.margin_17),
//                                    helper.getView(R.id.item_image));
                        }

                    }

                }

                break;
            case MultipleItem.IMGANDTEXT://上下结构的 图片 文字

//                helper.itemView.setLayoutParams(
//                        LayoutParmsUtils.getGroupParms(DisplayUtils.getDimen(R.dimen.margin_150)));

                helper.getView(R.id.item_image).setLayoutParams(LayoutParmsUtils.getLParms(
                        DisplayUtils.getDimen(R.dimen.margin_139)
                        ,0,0,0,0));

                TextView textView = helper.getView(R.id.item_tv);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextAppearance(R.style.Text3);
                }else {
                    textView.setTextAppearance(context,R.style.Text3);
                }

                if(item.getObject() instanceof Base){
                    Base itemBase = (Base) item.getObject();

                    helper.setText(R.id.item_tv,itemBase.getTitle());
                    GlideUtil.setGlideImage(context,itemBase.getCover(),
                            (ImageView) helper.getView(R.id.item_image));
                }

                textView.setGravity(Gravity.LEFT);

                break;
            case MultipleItem.IMGANDTEXTG2://上下结构的 图片 文字

//                helper.itemView.setLayoutParams(
//                        LayoutParmsUtils.getGroupParms(wight2()));

                LayoutParmsUtils.setHight(wight2(),helper.getView(R.id.item_image));

                if(item.getObject() instanceof Base){

                    Base itemBase = (Base) item.getObject();

                    helper.setText(R.id.item_tv,itemBase.getTitle());

                    GlideUtil.setGlideImageMake(context,itemBase.getCover(),
                            (ImageView) helper.getView(R.id.item_image));
                }
                break;
            case MultipleItem.IMGANDTEXTG4://上下结构的 图片 文字
//                final int p = DisplayUtils.getDimen(R.dimen.margin_18);
//                int p1 = DisplayUtils.getDimen(R.dimen.margin_6);
//                helper.itemView.setPadding(p,p1,p,p1);

//                helper.itemView.setLayoutParams(
//                        LayoutParmsUtils.getGroupParms(wight4()));

                if(item.getObject() instanceof Base){
                    Base itemBase = (Base) item.getObject();
                    helper.setText(R.id.item_tv,itemBase.getTitle());

                    GlideUtil.setGlideImageMake(context,itemBase.getCover(),
                            (ImageView) helper.getView(R.id.item_image));
                }
                break;
            case MultipleItem.TEXTDATE:// 左右结构的 图片 文字
                if(item.getObject() instanceof Base){
                    Base itemBase = (Base) item.getObject();

                    helper.setText(R.id.title_tv_news,itemBase.getTitle());
                    helper.setText(R.id.title_tv_date,itemBase.getPubdate());


                    GlideUtil.setGlideImageMake(context,itemBase.getCover(),
                            (ImageView) helper.getView(R.id.item_image));
                }
                break;
            case MultipleItem.RANKING://排行榜

                helper.setOnClickListener(R.id.gs_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TopListActivity.class);
                        intent.putExtra(TopListActivity.TOPLIST,TopListActivity.ONE);
                        context.startActivity(intent);
                    }
                });

                helper.setOnClickListener(R.id.zj_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, TopListActivity.class);
                        intent.putExtra(TopListActivity.TOPLIST,TopListActivity.TWO);
                        context.startActivity(intent);
                    }
                });

                if(true){
                    RecyclerView item_recycler_one = helper.getView(R.id.item_recycler_one);
                    RecyclerView item_recycler_two = helper.getView(R.id.item_recycler_two);

                    LinearLayoutManager linearLayoutManagerOne = new LinearLayoutManager(item_recycler_one.getContext());
                    LinearLayoutManager linearLayoutManagerTwo = new LinearLayoutManager(item_recycler_two.getContext());


                    item_recycler_one.setLayoutManager(linearLayoutManagerOne);
                    item_recycler_two.setLayoutManager(linearLayoutManagerTwo);

                    final QuickAdapter quickAdapterOne = new QuickAdapter(R.layout.item_rank){
                        @Override
                        protected void convert(BaseViewHolder helper, Object item) {

                            TextView textView = helper.getView(R.id.tv_data);
                            TextView tv_num = helper.getView(R.id.tv_num);

                            if(helper.getLayoutPosition() == 3 || helper.getLayoutPosition() == 4){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    textView.setTextAppearance(R.style.Text14);
                                    tv_num.setTextAppearance(R.style.Text15);
                                }else {
                                    textView.setTextAppearance(context,R.style.Text14);
                                    tv_num.setTextAppearance(context,R.style.Text15);
                                }
                            }else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    textView.setTextAppearance(R.style.Text4);
                                    tv_num.setTextAppearance(R.style.Text10);

                                }else {
                                    textView.setTextAppearance(context,R.style.Text4);
                                    tv_num.setTextAppearance(context,R.style.Text10);
                                }
                            }
                            if(item instanceof Base){
                                tv_num.setText((helper.getLayoutPosition() + 1) + " ");
                                textView.setText(((Base) item).getTitle());

                            }
                        }
                    };
                    quickAdapterOne.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Object item = quickAdapterOne.getItem(position);
                            if(item instanceof Base){

                                ComAll comAll = new ComAll();
                                comAll.setId(((Base) item).getId());

                                CommonUtils.getIntent(context, Arrays.asList(comAll),0, GlobalVariable.SIX);
                            }
                        }
                    });
                    final QuickAdapter quickAdapterTwo = new QuickAdapter(R.layout.item_rank){
                        @Override
                        protected void convert(BaseViewHolder helper, Object item) {

                            TextView textView = helper.getView(R.id.tv_data);
                            TextView tv_num = helper.getView(R.id.tv_num);

                            if(helper.getLayoutPosition() == 3 || helper.getLayoutPosition() == 4){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    textView.setTextAppearance(R.style.Text14);
                                    tv_num.setTextAppearance(R.style.Text15);
                                }else {
                                    textView.setTextAppearance(context,R.style.Text14);
                                    tv_num.setTextAppearance(context,R.style.Text15);
                                }
                            }else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    textView.setTextAppearance(R.style.Text4);
                                    tv_num.setTextAppearance(R.style.Text10);

                                }else {
                                    textView.setTextAppearance(context,R.style.Text4);
                                    tv_num.setTextAppearance(context,R.style.Text10);
                                }
                            }


                            if(item instanceof Base){
                                tv_num.setText((helper.getLayoutPosition() + 1) + " ");
                                textView.setText(((Base) item).getTitle());

                            }
                        }
                    };
                    quickAdapterTwo.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            Object item = quickAdapterTwo.getItem(position);
                            if(item instanceof Base){
                                Intent intent = new Intent(context, HeadGroupActivity.class);
                                intent.putExtra(HeadGroupActivity.HEADGROUP,((Base) item).getId());
                                context.startActivity(intent);
                            }
                        }
                    });
                    item_recycler_one.setAdapter(quickAdapterOne);
                    item_recycler_two.setAdapter(quickAdapterTwo);

                    if(item.getObject() instanceof Rank){
                        Rank rank = (Rank) item.getObject();

                        quickAdapterOne.replaceData(rank.getDay7audio());
                        quickAdapterTwo.replaceData(rank.getMonth7album());
                    }

                    final   int[] id = {R.string.本周, R.string.本月};

                    final  MagicIndicator magicIndicator = helper.itemView.findViewById(R.id.magic_indicator);
                    CommonNavigator commonNavigator = new CommonNavigator(helper.itemView.getContext());
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
                                    magicIndicator.onPageSelected(index);
                                    magicIndicator.onPageScrolled(index,0f,0);

                                    if(index == 0){
                                        if(item.getObject() instanceof Rank){
                                            Rank rank = (Rank) item.getObject();

                                            quickAdapterOne.replaceData(rank.getDay7audio());
                                            quickAdapterTwo.replaceData(rank.getMonth7album());
                                        }
                                    }else if(index == 1){
                                        if(item.getObject() instanceof Rank){
                                            Rank rank = (Rank) item.getObject();
                                            quickAdapterOne.replaceData(rank.getDay30audio());
                                            quickAdapterTwo.replaceData(rank.getMonth30album());
                                        }
                                    }

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
                }
                break;
            case MultipleItem.RECY://水平滚动
                RecyclerView recyclerView = helper.getView(R.id.item_recycler);

                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()
                        ,LinearLayoutManager.HORIZONTAL,false));
                if(item.getObject() instanceof List){
                    List list = (List) item.getObject();
                    final QuickAdapter quickAdapter = new QuickAdapter(R.layout.item_image_and_text_changed_one){
                        @Override
                        protected void convert(BaseViewHolder helper, Object item) {

                            super.convert(helper,item);
                        }
                    };
                    quickAdapter.replaceData(list);

                    quickAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                            Base base = (Base) quickAdapter.getData().get(position);
                            Intent intent = new Intent();
                            CommonUtils.setIntent(intent,context,base);

                        }
                    });

                    recyclerView.setAdapter(quickAdapter);
                }

                break;
        }
    }


    private int wight4(){
      int  wight = DisplayUtils.getWideP(4,0
                ,DisplayUtils.getDimen(R.dimen.margin_30));

        return wight;
    }
    private int wight2(){
        int  wight = DisplayUtils.getWideP(2,0
                ,DisplayUtils.getDimen(R.dimen.margin_5));

        return (int) (wight*1);
    }
}
