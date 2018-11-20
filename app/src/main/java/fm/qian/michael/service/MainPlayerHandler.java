package fm.qian.michael.service;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import com.xxbm.sbecomlibrary.net.base.BaseDataResponse;
import com.xxbm.sbecomlibrary.net.entry.response.ComAll;

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

                BaseDataResponse<ComAll> comAll = ( BaseDataResponse<ComAll>) msg.obj;
                service.setUpdataNUll(comAll);
                break;
            case REQGETSAVE:
                service.getSave();
                break;
        }


    }
}
