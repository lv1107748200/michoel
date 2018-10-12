package fm.qian.michael.net.entry;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class MySection extends SectionEntity<Object> {
    private boolean isMore;
    private Object object;
    public MySection(boolean isHeader, String header, boolean isMroe) {
        super(isHeader, header);
        this.isMore = isMroe;
    }
    public MySection(boolean isHeader, String header, boolean isMroe,Object object) {
        super(isHeader, header);
        this.isMore = isMroe;
        this.object = object;
    }

    public MySection(Object t) {
        super(t);
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean mroe) {
        isMore = mroe;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
