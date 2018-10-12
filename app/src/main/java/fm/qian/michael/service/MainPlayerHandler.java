package fm.qian.michael.service;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import static fm.qian.michael.service.MqService.REQDATA;
import static fm.qian.michael.service.MqService.REQGETSAVE;
import static fm.qian.michael.service.MqService.REQNFTION;

/*
 * lv   2018/10/12  主线程
 */
public class MainPlayerHandler extends Handler {

    private final WeakReference<MqService> mService;
    public MainPlayerHandler(final MqService service, final Looper looper) {
        super(looper);
        mService = new WeakReference<MqService>(service);
    }
    @Override
    public void handleMessage(final Message msg) {
        final MqService service = mService.get();
        if (service == null) {
            return;
        }

        switch (msg.what){
            case REQNFTION:
                service.updateNotification("main线程刷新");//开始播放
                break;
            case REQDATA:
                service.play(service.getComAll().getUrl());//获取地址后直接播放
                break;
            case REQGETSAVE:
                service.getSave();
                break;
        }


    }
}
