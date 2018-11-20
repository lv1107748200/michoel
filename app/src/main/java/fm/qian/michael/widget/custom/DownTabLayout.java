package fm.qian.michael.widget.custom;


import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xxbm.sbecomlibrary.utils.CheckUtil;

import java.util.ArrayList;
import java.util.List;

import fm.qian.michael.R;

/*
 * lv   2018/11/8
 */
public class DownTabLayout extends LinearLayout {

    private List<DownTabViewHodler> tabViewHodlers;
    private DownCallBack downCallBack;


    public DownTabLayout(Context context) {
        this(context,null);
    }

    public DownTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DownTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        tabViewHodlers = new ArrayList<>();
    }


    public void setDownTabView(List<String> list, DownCallBack downCallBack){

        this.downCallBack = downCallBack;

        if(!CheckUtil.isEmpty(list)){

            removeAllViews();
            tabViewHodlers.clear();


            for(int i=0, j = list.size(); i<j; i++){

                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_down_tab,null,false);
                view.setTag(i);

                DownTabViewHodler downTabViewHodler = new DownTabViewHodler();

                downTabViewHodler.contextView = view;
                downTabViewHodler.title = view.findViewById(R.id.tv_title);
                downTabViewHodler.num = view.findViewById(R.id.tv_num);

                tabViewHodlers.add(downTabViewHodler);

                downTabViewHodler.title.setText(list.get(i));

                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(null != DownTabLayout.this.downCallBack)
                            DownTabLayout.this.downCallBack.selCallBack((Integer) v.getTag());
                    }
                });

                addView(view);

            }
        }

    }

    public void setNum(int num,String s){
        if(num>=0 && num < tabViewHodlers.size()) {
            DownTabViewHodler downTabViewHodler = tabViewHodlers.get(num);
            downTabViewHodler.num.setText(s);
        }
    }

    public void selectDownTab(int sel){
        for(int i = 0, j = tabViewHodlers.size(); i < j ;i++){
            DownTabViewHodler downTabViewHodler = tabViewHodlers.get(i);
            if(sel == i){
                if(downTabViewHodler.contextView.isSelected()){

                }else {
                    downTabViewHodler.contextView.setSelected(true);
                }
            }else {
                if(downTabViewHodler.contextView.isSelected()){
                    downTabViewHodler.contextView.setSelected(false);
                }
            }
        }
    }

    public static class DownTabViewHodler{
        private View contextView;
        private TextView title;
        private TextView num;

        public View getContextView() {
            return contextView;
        }

        public void setContextView(View contextView) {
            this.contextView = contextView;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public TextView getNum() {
            return num;
        }

        public void setNum(TextView num) {
            this.num = num;
        }
    }

    public interface DownCallBack{
        void selCallBack(int num);
    }

}
