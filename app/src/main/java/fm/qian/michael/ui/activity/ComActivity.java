package fm.qian.michael.ui.activity;

import android.view.View;
import android.widget.TextView;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;


/*
 * lv   2018/9/8
 */
public class ComActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView titleTv;
    @OnClick({R.id.base_left_layout})
    public  void  onClick(View view){
        switch (view.getId()){
            case R.id.base_left_layout:
                finish();
                break;
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_com;
    }

    @Override
    public void initTitle() {
        super.initTitle();
        //setStatusBar(statusBar);
        titleTv.setText("播放");
    }

    @Override
    public void initView() {
        super.initView();
    }
}
