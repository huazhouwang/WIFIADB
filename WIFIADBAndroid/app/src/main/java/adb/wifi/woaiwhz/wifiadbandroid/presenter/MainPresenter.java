package adb.wifi.woaiwhz.wifiadbandroid.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import adb.wifi.woaiwhz.wifiadbandroid.MyApp;
import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;
import adb.wifi.woaiwhz.wifiadbandroid.tools.PortMonitor;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public class MainPresenter {
    private static final int WIFI_NO_READY = 1;
    private static final int WIFI_READY = 1 << 1;
    private static final int PORT_NO_READY = 1 << 2;
    private static final int PORT_READY = 1 << 3;

    private final PortMonitor mMonitor;
    private final MainView mViewLayer;

    @IntDef({PORT_READY, PORT_NO_READY, WIFI_NO_READY,WIFI_READY})
    @Retention(SOURCE)
    private @interface STATE{}
    @STATE private int mState;

    private boolean mRunning;
    private BroadcastReceiver mReceiver;

    public MainPresenter(MainView viewLayer){
        mViewLayer = viewLayer;
        final Handler handler = new MyHandler(Looper.getMainLooper());
        mMonitor = new PortMonitor(handler);
        mRunning = false;
        mReceiver = new WifiChangeReceiver();

        MyApp.getContext().registerReceiver(mReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void check(){
        if(mRunning){
            return;
        }

        final boolean wifiReady = WiFiModule.getInstance().isReady();

        if(wifiReady){
            wifiReady();
        }else {
            wifiNoReady();
        }
    }

    private void wifiNoReady(){
        mState = WIFI_NO_READY;
        mViewLayer.onWifiNoReady();
    }

    private void wifiReady(){
        mState = WIFI_READY;
        mViewLayer.onWifiReady();
        checkPortState();
    }

    public void togglePortState(){
        if(mState == PORT_READY){
            changePortState(PORT_NO_READY);
        }else if(mState == PORT_NO_READY){
            changePortState(PORT_READY);
        }
    }

    private void changePortState(@STATE int newState){
        if(cannotRun()){
            return;
        }

        if (newState == PORT_NO_READY){
            mRunning = mMonitor.stopPort();
        }else if(newState == PORT_READY){
            mRunning = mMonitor.startPort();
        }

        mViewLayer.pageLoading(mRunning);
    }

    private void checkPortState(){
        if (cannotRun()){
            return;
        }

        mRunning = mMonitor.checkPort();
        mViewLayer.pageLoading(mRunning);
    }

    private boolean cannotRun(){
        return mRunning || mState < WIFI_READY;
    }

    public void onDestroy(){
        mMonitor.interrupt();
        MyApp.getContext().unregisterReceiver(mReceiver);
    }

    private class MyHandler extends Handler{
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void dispatchMessage(Message msg) {
            final int what = msg.what;

            switch (what){
                case PortMonitor.PORT_READY_NOW:
                    onPortReady();
                    break;

                case PortMonitor.PORT_NO_READY_NOW:
                    onPortNoReady();
                    break;

                default:
                    break;
            }

            mViewLayer.pageLoading(false);
            mRunning = false;
        }
    }

    private void onPortReady() {
        mState = PORT_READY;
        mViewLayer.onPortReady(WiFiModule.getInstance().getIp());
    }

    private void onPortNoReady(){
        mState = PORT_NO_READY;
        mViewLayer.onPortNoReady();
    }

    private class WifiChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean wifiReady = WiFiModule.getInstance().isReady();

            if(wifiReady){
                if(mState < WIFI_READY){
                    wifiReady();
//                    interrupt();
                }
            }else if(mState >= WIFI_READY){
                wifiNoReady();
                interrupt();
            }
        }

        private void interrupt(){
            mMonitor.interrupt();
            mRunning = false;
        }
    }

    public interface MainView{
        void pageLoading(boolean show);
        void onWifiNoReady();
        void onWifiReady();
        void onPortReady(String ip);
        void onPortNoReady();
    }
}
