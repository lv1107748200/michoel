package fm.qian.michael.ui.adapter;


import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import fm.qian.michael.R;
import fm.qian.michael.net.entry.MultipleItem;

/*
 * lv   2018/10/16
 */
public class MultipleItemPayOrAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int WEB = 0;
    public static final int LIST = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MultipleItemPayOrAdapter(List data) {
        super(data);

        addItemType(WEB, R.layout.item_pay_brief);
        addItemType(LIST, R.layout.item_sel_voice);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case WEB:

                break;
            case LIST:

                break;
        }
    }
}
