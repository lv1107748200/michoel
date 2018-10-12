package fm.qian.michael.widget.custom;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by 吕 on 2018/1/22.
 */

public class AverageTabLayout extends LinearLayout {



    public AverageTabLayout(Context context) {
        this(context,null);
    }

    public AverageTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AverageTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    public static class AverageTabData{
        private @ColorInt
        int colorText;
        private @ColorInt
        int colorLine;

        private String text;



    }

}
