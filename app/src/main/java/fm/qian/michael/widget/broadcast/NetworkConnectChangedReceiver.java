package fm.qian.michael.widget.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.hr.bclibrary.utils.CheckUtil;
import com.hr.bclibrary.utils.NLog;

/**
 * Created by 吕 on 2018/1/12.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {

    private static final String TAG = "xujun";
    public static final String TAG1 = "xxx";


    private BroadcastCallBack broadcastCallBack;

    public void setBroadcastCallBack(BroadcastCallBack broadcastCallBack) {
        this.broadcastCallBack = broadcastCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            NLog.e(TAG1, "wifiState" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    //APP.getInstance().setEnablaWifi(false);

                    break;
                case WifiManager.WIFI_STATE_DISABLING:

                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                   // APP.getInstance().setEnablaWifi(true);

                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:

                    break;
                default:

                    break;


            }
        }
        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager
        // .WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
        // 当然刚打开wifi肯定还没有连接到有效的无线
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                NLog.e(TAG1, "isConnected" + isConnected);
                if (isConnected) {
                   // APP.getInstance().setWifi(true);

                } else {

                   // APP.getInstance().setWifi(false);
                }
            }
        }
        // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
        // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
        // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NLog.e(TAG1, "CONNECTIVITY_ACTION");

            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                       // APP.getInstance().setWifi(true);
                        NLog.e(TAG, "当前WiFi连接可用 ");
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to the mobile provider's data plan
                       // APP.getInstance().setMobile(true);
                        NLog.e(TAG, "当前移动网络连接可用 ");
                    }
                    if(!CheckUtil.isEmpty(broadcastCallBack)){
                        broadcastCallBack.setState(true);
                    }
                } else {
                    NLog.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");

                    if(!CheckUtil.isEmpty(broadcastCallBack)){
                        broadcastCallBack.setState(false);
                    }
                }


                NLog.e(TAG1, "info.getTypeName()" + activeNetwork.getTypeName());
                NLog.e(TAG1, "getSubtypeName()" + activeNetwork.getSubtypeName());
                NLog.e(TAG1, "getState()" + activeNetwork.getState());
                NLog.e(TAG1, "getDetailedState()"
                        + activeNetwork.getDetailedState().name());
                NLog.e(TAG1, "getDetailedState()" + activeNetwork.getExtraInfo());
                NLog.e(TAG1, "getType()" + activeNetwork.getType());
            } else {   // not connected to the internet
                NLog.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
               // APP.getInstance().setWifi(false);
               // APP.getInstance().setMobile(false);
               // APP.getInstance().setConnected(false);
                if(!CheckUtil.isEmpty(broadcastCallBack)){
                    broadcastCallBack.setState(false);
                }
            }


        }
    }

    public interface BroadcastCallBack{
        public void setState(boolean is);
    }

}
