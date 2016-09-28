package adb.wifi.woaiwhz.wifiadbandroid.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;

import adb.wifi.woaiwhz.wifiadbandroid.MyApp;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public class WiFiModule {
    private static WiFiModule mInstance;
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;


    private WiFiModule(Context context){
        context = context.getApplicationContext();

        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static WiFiModule getInstance(){
        if (mInstance == null){
            mInstance = new WiFiModule(MyApp.getContext());
        }

        return mInstance;
    }

    public @Nullable String getIp(){
        if(isReady()){
            final WifiInfo info = mWifiManager.getConnectionInfo();
            final String ip = format(info.getIpAddress());

            return ip;
        }else {
            return null;
        }
    }

    public void enable(boolean enable){
        mWifiManager.setWifiEnabled(enable);
    }

    public boolean isEnable(){
        return mWifiManager.isWifiEnabled();
    }

    public boolean isReady(){
        boolean enable = isEnable();

        if(enable){
            final NetworkInfo.State state = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            enable = (state == NetworkInfo.State.CONNECTED);
        }

        return enable;
    }

    private static String format(final int ip) {
        final String SPLIT = ".";

        try {
            return  (ip & 0xFF) + SPLIT +
                    ((ip >> 8) & 0xFF) + SPLIT +
                    ((ip >> 16) & 0xFF) + SPLIT +
                    (ip >> 24 & 0xFF);
        }catch (Exception e){
            return null;
        }
    }
}
