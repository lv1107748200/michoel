package fm.qian.michael.net.entry;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class MultipleItem implements MultiItemEntity {

    public static final int BAN = 0;//轮播图
    public static final int HEAD = 1;
    public static final int IMGANDTEXT = 2;
    public static final int TEXTDATE = 3;
    public static final int RANKING = 4;//排行榜
    public static final int RECY = 5;//滚动的
    public static final int RECYG = 6;//网格
    public static final int IMGANDTEXTG2 = 7;
    public static final int IMGANDTEXTG4 = 8;

    public static final int ONE = 1;//网格
    public static final int TWO = 2;//网格
    public static final int FOUR = 4;//网格


    private int itemType;
    private int spanSize;

    private Object object;

    public MultipleItem(int itemType, int spanSize, String content) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.content = content;
    }
    public MultipleItem(int itemType, int spanSize, Object content) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.object = content;
    }

    public MultipleItem(int itemType, int spanSize) {
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    public Object getObject() {
        return object;
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
