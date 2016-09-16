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

    @IntDef({PORT_READY, PORT_NO_READY, WIFI_NO_READY,WIFI_READY})
    @Retention(SOURCE)
    private @interface STATE{}

    private final Handler mHandler;
    private final PortMonitor mMonitor;
    private final MainView mViewLayer;
    @STATE private int mState;
    private boolean mRunning;
    private BroadcastReceiver mReceiver;

    public MainPresenter(MainView viewLayer){
        mViewLayer = viewLayer;
        mHandler = new MyHandler(Looper.getMainLooper());
        mMonitor = new PortMonitor(mHandler);
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
        if(!canRun()){
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
        if (!canRun()){
            return;
        }

        mRunning = mMonitor.checkPort();
        mViewLayer.pageLoading(mRunning);
    }

    private boolean canRun(){
        return !mRunning && mState >= WIFI_READY;
    }

    public void onDestroy(){
        mMonitor.interrupt();
        MyApp.getContext().unregisterReceiver(mReceiver);
        mReceiver = null;
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

            super.dispatchMessage(msg);
        }
    }

    private void onPortReady() {
        mState = PORT_READY;
        mViewLayer.onPortReady(WiFiModule.getInstance().getIp());
        mViewLayer.pageLoading(false);
        mRunning = false;
    }

    private void onPortNoReady(){
        mState = PORT_NO_READY;
        mViewLayer.onPortNoReady();
        mViewLayer.pageLoading(false);
        mRunning = false;
    }

    private class WifiChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mMonitor.interrupt();
            mRunning = false;

            final boolean wifiEnable = WiFiModule.getInstance().isReady();

            if(wifiEnable){
                if(mState < WIFI_READY){
                    wifiReady();
                }
            }else if(mState >= WIFI_READY){
                wifiNoReady();
            }
        }
    }

    public interface MainView{
        void pageLoading(boolean display);
        void onWifiNoReady();
        void onWifiReady();
        void onPortReady(String ip);
        void onPortNoReady();
    }
}
