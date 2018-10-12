package fm.qian.michael.widget.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fm.qian.michael.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 吕 on 2017/11/16.
 */

public class BottomBarLayout extends LinearLayout implements View.OnClickListener {

    private XCViewPager viewPager;
    private List<View> views;

    private BottomCallBack bottomCallBack,bottomCallBackFragment;

    public void setBottomCallBack(BottomCallBack bottomCallBack) {
        this.bottomCallBack = bottomCallBack;
    }
    public void setBottomCallBackFragment(BottomCallBack bottomCallBack) {
        this.bottomCallBackFragment = bottomCallBack;
    }


    public BottomBarLayout(Context context) {
        this(context,null);
    }

    public BottomBarLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        views = new ArrayList<>();
    }
    public void init(Context context, List<DataList> dataLists){

        for(int i =0 ,j = dataLists.size() ; i<j; i++){
          View view =  View.inflate(context, fm.qian.michael.R.layout.bottom_bar_path,null);


            DataList dataList = dataLists.get(i);

            ImageView imageView = view.findViewById(fm.qian.michael.R.id.tab_image);
            TextView tab_tv = view.findViewById(fm.qian.michael.R.id.tab_tv);

            imageView.setBackgroundResource(dataList.drable);
            tab_tv.setText(dataList.what);

            view.setTag(i);
            view.setOnClickListener(this);
            views.add(view);

            addView(view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,1));
        }
    }

    public void setViewPager(XCViewPager viewPager) {
        this.viewPager = viewPager;
        if(this.viewPager != null){
            this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setBomSel(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    public void setViewPagerSelect(){


    }

    public void setSelect(int point){

        if(null != bottomCallBack){
            bottomCallBack.onBottomTab(point);
        }


        if(null != viewPager){
            viewPager.setCurrentItem(point,false);
        }


    }
    public void setBomSel(int point){
        for(int i = 0; i<getChildCount(); i++){
            if( point == i){
                views.get(i).setSelected(true);
            }else {
                views.get(i).setSelected(false);
            }
        }
    }

    private void setBold(TextView textView, boolean is){
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(is);
    }

    @Override
    public void onClick(View v) {

        setSelect((Integer) v.getTag());

    }




    class TabViewHolder{
        public LinearLayout layout_tab;
        public ImageView tab_image;
        public TextView tab_tv;
    }
    public static class DataList{
        public int drable;
        public int what;

        public DataList() {
        }

        public DataList(int drable, int what) {
            this.drable = drable;
            this.what = what;
        }
    }

    public interface BottomCallBack{

        void onSafetyEnterprise();

        void onBottomTab(int num);

    }
}
