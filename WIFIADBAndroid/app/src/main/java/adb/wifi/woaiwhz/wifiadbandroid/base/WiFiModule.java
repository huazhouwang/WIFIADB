package adb.wifi.woaiwhz.wifiadbandroid.base;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;

import adb.wifi.woaiwhz.wifiadbandroid.MyApp;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public class WiFiModule {
    private static WiFiModule mInstance;
    private WifiManager mManager;

    private WiFiModule(Context context){
        mManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    public static WiFiModule getInstance(){
        if (mInstance == null){
            mInstance = new WiFiModule(MyApp.getContext());
        }

        return mInstance;
    }

    private @Nullable String getIp(){
        if(isEnable()){
            final WifiInfo info = mManager.getConnectionInfo();
            final String ip = format(info.getIpAddress());

            return ip;
        }else {
            return null;
        }
    }

    public boolean isEnable(){
        return mManager.isWifiEnabled();
    }

    private static String format(final int ip) {
        final String SPLIT = ".";

        return (ip & 0xFF ) + SPLIT +
                ((ip >> 8 ) & 0xFF) + SPLIT +
                ((ip >> 16 ) & 0xFF) + SPLIT +
                ( ip >> 24 & 0xFF) ;
    }
}
