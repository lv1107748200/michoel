package fm.qian.michael.ui.activity;


import android.content.Intent;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/*
 * lv   2018/9/8
 */
public class SplashActivity extends BaseActivity {


    @Override
    public int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void initTitle() {
        super.initTitle();
    }

    @Override
    public void initView() {
        super.initView();

        Observable.timer(2, TimeUnit.SECONDS).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
