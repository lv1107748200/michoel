package fm.qian.michael.widget.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

/**
 * Created by 吕 on 2018/2/1.
 */

public class TextViewWithoutPadding extends android.support.v7.widget.AppCompatTextView {

    private final Paint mPaint = new Paint();

    private final Rect mBounds = new Rect();

    public TextViewWithoutPadding(Context context) {
        super(context);
    }

    public TextViewWithoutPadding(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewWithoutPadding(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        final String text = calculateTextParams();

        final int left = mBounds.left;
        final int bottom = mBounds.bottom;
        mBounds.offset(-mBounds.left, -mBounds.top);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getCurrentTextColor());
        canvas.drawText(text, -left, mBounds.bottom - bottom, mPaint);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateTextParams();

       // setMeasuredDimension(mBounds.width() + 1, -mBounds.top + 1);
        setMeasuredDimension(mBounds.width() + 1, mBounds.height()+1);
    }

    private String calculateTextParams() {
        final String text = getText().toString();
        final int textLength = text.length();
        mPaint.setTextSize(getTextSize());
        mPaint.getTextBounds(text, 0, textLength, mBounds);//返回包围整个字符串的最小的一个Rect区域
        if (textLength == 0) {
            mBounds.right = mBounds.left;
        }

        return text;
    }
}
