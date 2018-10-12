package fm.qian.michael.ui.activity;

import android.view.View;
import android.widget.TextView;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import fm.qian.michael.base.activity.BaseActivity;


/*
 * lv   2018/9/8
 */
public class ComActivity extends BaseActivity {
    @BindView(fm.qian.michael.R.id.status_bar)
    View statusBar;
    @BindView(fm.qian.michael.R.id.title_tv)
    TextView titleTv;
    @OnClick({fm.qian.michael.R.id.base_left_layout})
    public  void  onClick(View view){
        switch (view.getId()){
            case fm.qian.michael.R.id.base_left_layout:
                finish();
                break;
        }
    }

    @Override
    public int getLayout() {
        return fm.qian.michael.R.layout.activity_com;
    }

    @Override
    public void initTitle() {
        super.initTitle();
        setStatusBar(statusBar);
        titleTv.setText("播放");
    }

    @Override
    public void initView() {
        super.initView();
    }
}
