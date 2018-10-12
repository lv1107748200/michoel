package fm.qian.michael.widget.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.text.style.ImageSpan;

import fm.qian.michael.R;


/**
 * Created by 吕 on 2018/2/6.
 */

public class CustomImageSpan extends ImageSpan {
    //自定义对齐方式--与文字中间线对齐
    private int ALIGN_FONTCENTER = 2;

    private String textToDraw;
    private Paint mPaint;
    private Rect mRect;
    private GradientDrawable mDrawable;

    private int magin = 60;

    public CustomImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public CustomImageSpan(Context context, int resourceId, int verticalAlignment, String text, int ma, @ColorInt int color) {
        super(context, resourceId, verticalAlignment);
        mDrawable = (GradientDrawable) ContextCompat.getDrawable(context,resourceId);

        mDrawable.setColor(color);
        init(text,context,ma,color);

    }

    private void  init(String text, Context context, int ma, @ColorInt int color){

        textToDraw = text;
        magin = ma;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(context.getResources().getDimension(fm.qian.michael.R.dimen.size_11));                         //绘制的文本大小
        mPaint.setColor(Color.WHITE);//颜色
        mPaint.setTextAlign(Paint.Align.LEFT);

        mRect = new Rect();

        //获取文本的区域（宽、高）。参数依次是：要绘制的文本，从第几个文字开始绘制，到第几个文本截止，存储文字宽高信息的矩形
        mPaint.getTextBounds(textToDraw, 0, textToDraw.length(), mRect);


        mDrawable.setBounds(0, 0, mRect.width() + magin, mDrawable.getIntrinsicHeight());


    }



    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                     Paint paint) {

        //draw 方法是重写的ImageSpan父类 DynamicDrawableSpan中的方法，在DynamicDrawableSpan类中，虽有getCachedDrawable()，
        // 但是私有的，不能被调用，所以调用ImageSpan中的getrawable()方法，该方法中 会根据传入的drawable ID ，获取该id对应的
        // drawable的流对象，并最终获取drawable对象
        Drawable drawable = getDrawable(); //调用imageSpan中的方法获取drawable对象
        canvas.save();
        Rect rectD = drawable.getBounds();

        //获取画笔的文字绘制时的具体测量数据
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();

        //系统原有方法，默认是Bottom模式)
        int transY = bottom - rectD.bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= fm.descent;
        } else if (mVerticalAlignment == ALIGN_FONTCENTER) {    //此处加入判断， 如果是自定义的居中对齐
            //与文字的中间线对齐（这种方式不论是否设置行间距都能保障文字的中间线和图片的中间线是对齐的）
            // y+ascent得到文字内容的顶部坐标，y+descent得到文字的底部坐标，（顶部坐标+底部坐标）/2=文字内容中间线坐标
            transY = ((y + fm.descent) + (y + fm.ascent)) / 2 - drawable.getBounds().bottom / 2;
        }

        canvas.translate(x, transY);
        drawable.draw(canvas);

        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (rectD.height() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(textToDraw,rectD.centerX() - mRect.width()/2,baseline,mPaint);

        canvas.restore();
    }

    /**
     * 重写getSize方法，只有重写该方法后，才能保证不论是图片大于文字还是文字大于图片，都能实现中间对齐
     */
    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();

        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }


    @Override
    public Drawable getDrawable() {
        return mDrawable ;
    }
}
