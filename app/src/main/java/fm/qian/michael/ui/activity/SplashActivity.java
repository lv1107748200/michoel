package fm.qian.michael.ui.activity;


import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import fm.qian.michael.R;
import fm.qian.michael.base.activity.BaseActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/*
 * lv   2018/9/8
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            //获取ActivityManager
            ActivityManager mAm = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
            //获得当前运行的task
            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo rti : taskList) {
                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                if(rti.topActivity.getPackageName().equals(getPackageName())) {
                    mAm.moveTaskToFront(rti.id,ActivityManager.MOVE_TASK_WITH_HOME);
                    finish();
                    break;
                }
            }
        }
        super.onCreate(savedInstanceState);
    }

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
