package fm.qian.michael.ui.adapter;


import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import fm.qian.michael.R;
import fm.qian.michael.net.entry.DataServer;
import fm.qian.michael.net.entry.Video;
import fm.qian.michael.net.entry.response.Base;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;

import fm.qian.michael.net.entry.DataServer;
import fm.qian.michael.net.entry.Video;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.LayoutParmsUtils;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class QuickAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

    int wight = -1;

    public QuickAdapter(int layout) {
        super(layout);
    }
    public QuickAdapter(int layout,int dataSize) {
        super(layout, DataServer.getSampleData(dataSize));
    }
    @Override
    protected void convert(BaseViewHolder helper,Object  item) {
//
//        helper.itemView.setLayoutParams(
//                LayoutParmsUtils.getGroupParms(wight(),hight()));

        if(wight < 0){
            wight();
        }

        helper.itemView.setLayoutParams(
                LayoutParmsUtils.getGroupParmsW(wight));
        LayoutParmsUtils.setHight(hight(),helper.getView(R.id.item_image));


       // helper.getView(R.id.item_image).setLayoutParams(LayoutParmsUtils.getLParms(wight(),hight(),0,0,0,0));

//        if(helper.getLayoutPosition() % 2 == 0){
//            helper.setText(R.id.item_tv,"一行");
//        }else {
//            helper.setText(R.id.item_tv,item.getName());
//        }

        if(item instanceof Video){
            helper.setText(fm.qian.michael.R.id.item_tv,((Video) item).getName());
            GlideUtil.setGlideImage(helper.itemView.getContext(),((Video) item).getImg(),
                    (ImageView) helper.getView(fm.qian.michael.R.id.item_image), fm.qian.michael.R.drawable.playbg);
        }else if(item instanceof Base){
            Base itemBase = (Base) item;

            helper.setText(R.id.item_tv,itemBase.getTitle());
            GlideUtil.setGlideImageMake(helper.itemView.getContext(),itemBase.getCover(),
                    (ImageView) helper.getView(R.id.item_image));
        }


    }

    private int wight(){
         wight = DisplayUtils.getWideP(3,0
                ,DisplayUtils.getDimen(R.dimen.margin_20));

        return wight;
    }

    private int hight(){

        return wight/3*4;
    }
}
