package fm.qian.michael.ui.adapter;


import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import fm.qian.michael.R;
import fm.qian.michael.net.entry.MySection;
import com.xxbm.sbecomlibrary.net.entry.response.Base;
import fm.qian.michael.utils.DisplayUtils;
import fm.qian.michael.utils.GlideUtil;
import fm.qian.michael.utils.LayoutParmsUtils;

import java.util.List;

import fm.qian.michael.net.entry.MySection;

/**
 *
 */
public class SectionAdapter extends BaseSectionQuickAdapter<MySection, BaseViewHolder> {

    public SectionAdapter(int layoutResId, int sectionHeadResId, List data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, final MySection item) {

        helper.setText(R.id.head_tv, item.header);

        helper.setVisible(R.id.head_image, item.isMore());
        helper.setVisible(R.id.head_tv_message, item.isMore());


    }


    @Override
    protected void convert(BaseViewHolder helper, MySection item) {
        helper.itemView.setLayoutParams(
                LayoutParmsUtils.getGroupParms(hight()));

        helper.setGone(R.id.item_tv, false);

//        ImageView imageView = helper.getView(R.id.item_image);
//        imageView.setAdjustViewBounds(true);

        if(item.t instanceof Base){
            GlideUtil.setGlideImageMake(helper.itemView.getContext(),((Base) item.t).getCover(),
                    (ImageView) helper.getView(R.id.item_image));
        }
    }
    private int hight(){
        int wight = DisplayUtils.getWideP(3,0
                ,DisplayUtils.getDimen(fm.qian.michael.R.dimen.margin_5));

        return wight;
    }
}
