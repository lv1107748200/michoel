package fm.qian.michael.service;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import fm.qian.michael.net.base.BaseDataResponse;
import fm.qian.michael.net.entry.response.ComAll;

import static fm.qian.michael.service.MqService.REQDATA;
import static fm.qian.michael.service.MqService.REQICON;

/*
 * lv   2018/10/12 子线程
 */
public class MusicPlayerHandler extends Handler {

    private final WeakReference<MqService> mService;

    public MusicPlayerHandler(final MqService service, final Looper looper) {
        super(looper);
        mService = new WeakReference<MqService>(service);
    }
    @Override
    public void handleMessage(final Message msg) {
        final MqService service = mService.get();
        if (service == null) {
            return;
        }
        synchronized (service) {
            switch (msg.what) {
                case REQDATA:


                    break;
                case REQICON:



                    break;
            }
        }

    }


}
