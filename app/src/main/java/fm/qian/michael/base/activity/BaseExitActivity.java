package fm.qian.michael.base.activity;

import android.content.Intent;
import android.view.KeyEvent;


import java.util.concurrent.TimeUnit;

import fm.qian.michael.R;
import fm.qian.michael.utils.NToast;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


/**
 * Created by 吕 on 2017/11/9.
 */

public class BaseExitActivity extends BaseActivity {
    private boolean exit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!exit) {
                NToast.shortToast(R.string.press_to_exit_home);
                exit = true;

                Observable.timer(2, TimeUnit.SECONDS).
                        observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                exit = false;
                            }
                        });

                return true;
            }else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return super.onKeyDown(KeyEvent.KEYCODE_BACK, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
