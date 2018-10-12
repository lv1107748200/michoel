package fm.qian.michael.ui.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;

/*
 * lv   2018/10/12
 */
public class LockActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);




        setContentView(R.layout.activity_lock);
    }

//
//    @Override
//    public void onBackPressed() {
//        // do nothing
//    }


//    @Override
//    public int getLayout() {
//        return R.layout.activity_lock;
//    }
}
