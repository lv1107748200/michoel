package fm.qian.michael.common;


import android.view.View;
import android.widget.TextView;

/*
 * lv   2018/10/11 用于刷新
 */
public class BaseDownViewHolder {
    public BaseDownViewHolder() {
    }

    public BaseDownViewHolder(int id, View view, TextView textView) {
        this.id = id;
        this.view = view;
        this.textView = textView;
    }

    public BaseDownViewHolder(int id, int position, View view, TextView textView) {
        this.id = id;
        this.position = position;
        this.view = view;
        this.textView = textView;
    }

    private int id;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private View view;

   private TextView textView;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
