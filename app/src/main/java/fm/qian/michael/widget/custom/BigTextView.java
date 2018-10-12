package fm.qian.michael.widget.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import fm.qian.michael.R;


/**
 * Created by 吕 on 2018/2/6.
 */

public class BigTextView extends View {
    private String mText ="115";
    private Paint mPaint;
    private Rect mTextBounds = new Rect();

    public BigTextView(Context context) {
        this(context,null);
    }

    public BigTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BigTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        int textSize = (int) context.getResources().getDimension(fm.qian.michael.R.dimen.size_115);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(textSize);
        setPadding(0, 0, 0, 0);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureTextBounds();
        //setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        setMeasuredDimension(mTextBounds.width() + 1, mTextBounds.height()+1);
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int result = size;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                int width = mTextBounds.width() + getPaddingLeft() + getPaddingRight();
                result = Math.min(width, size);
                break;
            case MeasureSpec.UNSPECIFIED:
                result = mTextBounds.width() + getPaddingLeft() + getPaddingRight();
                break;
            default:
                break;
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result = size;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                int height = mTextBounds.height() + getPaddingTop() + getPaddingBottom();
                result = Math.min(height, size);
                break;
            case MeasureSpec.UNSPECIFIED:
                result = mTextBounds.height() + getPaddingTop() + getPaddingBottom();
                break;
            default:
                break;
        }
        return result;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(null == mText)
            return;
//        int x = (getWidth() - mTextBounds.width()) / 2;
//        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
//        int y = (int) ((getHeight() - fontMetrics.bottom - fontMetrics.top) / 2);
//
//        // 画底层
        mPaint.setColor(ContextCompat.getColor(getContext(), fm.qian.michael.R.color.color_f5a));
//        canvas.drawText(mText, x, y, mPaint);

        measureTextBounds();

        final int left = mTextBounds.left;
        final int bottom = mTextBounds.bottom;
        mTextBounds.offset(-mTextBounds.left, -mTextBounds.top);
        canvas.drawText(mText, -left, mTextBounds.bottom - bottom, mPaint);
    }

    private void measureTextBounds() {
        mPaint.getTextBounds(mText, 0, mText == null ? 0 : mText.length(), mTextBounds);
    }

    public void setmText(String text){
        mText = text;
        requestLayout();
        invalidate();
    }
}
