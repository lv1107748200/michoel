package fm.qian.michael.base.activity;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;



import butterknife.BindView;
import fm.qian.michael.R;

/*
 * lv   2018/9/8
 */
public class BaseIntensifyActivity extends BaseActivity {


    @BindView(R.id.status_bar)
    View statusBar;
    @BindView(R.id.title_tv)
    TextView titleTv;

    @Override
    public void initView() {
        super.initView();
        setStatusBar(statusBar);
    }

    public void setTitleTv(String tv) {
        titleTv.setText(tv);
    }


    public View getEmpty(){
        View view = LayoutInflater.from(this).inflate(R.layout.empty_view,null,false);

        return view;
    }
}
