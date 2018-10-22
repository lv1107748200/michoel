package fm.qian.michael.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.hr.bclibrary.utils.NLog;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.tencent.smtt.sdk.QbSdk;

import fm.qian.michael.common.GlobalVariable;
import fm.qian.michael.db.UseData;
import fm.qian.michael.service.MusicPlayerManger;
import fm.qian.michael.widget.single.DownManger;

/**
 * Created by 吕 on 2017/10/26.
 */

public class BaseApplation extends Application {
    private static BaseApplation baseApp = null;
    private AppComponent mAppComponent;

    static {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NLog.setDebug(true);
        baseApp = this;
        mAppComponent = DaggerAppComponent.create();

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            //sp
           // SPUtils.init(this);

            FileDownloader.setupOnApplicationOnCreate(this)
                    .connectionCreator(new FileDownloadUrlConnection
                            .Creator(new FileDownloadUrlConnection.Configuration()
                            .connectTimeout(15_000) // set connection timeout.
                            .readTimeout(15_000) // set read timeout.
                    )).commit();
            FileDownloader.setGlobalPost2UIInterval(50);//设置ui 刷新时间间隔 防止掉帧
            FileDownloader.getImpl().setMaxNetworkThreadCount(1);//设置最大并行下载数量

            DownManger.bindService();

            FlowManager.init(new FlowConfig.Builder(this).build());


            String type = UseData.getUseData().getType();
            fm.qian.michael.utils.NLog.e(fm.qian.michael.utils.NLog.PLAYER,"获取状态 --->" + type);
            if(!GlobalVariable.ZERO.equals(type)){
                UseData.setInit(GlobalVariable.ONE);
            }

            MusicPlayerManger.bindStartByContext(BaseApplation.getBaseApp(),null);//启动播放器服务
        }


        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                NLog.e(NLog.TAGOther, " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    public static BaseApplation getBaseApp() {
        return baseApp;
    }

    public AppComponent getAppComponent(){
        return mAppComponent;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
