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
    private static final int MONITORING = 1;
    private static final int NO_MONITORING = 1 << 1;
    private static final int WIFI_NO_READY = 1 << 2;
    private static final int WIFI_READY = 1 << 4;

    @IntDef({MONITORING, NO_MONITORING, WIFI_NO_READY,WIFI_READY})
    @Retention(SOURCE)
    private @interface STATE{}

    private final Handler mHandler;
    private final PortMonitor mMonitor;
    private final MainView mViewLayer;
    private @STATE int mState;
    private boolean mIsRunning;
    private BroadcastReceiver mReceiver;

    public MainPresenter(MainView viewLayer){
        mViewLayer = viewLayer;
        mHandler = new MyHandler(Looper.getMainLooper());
        mMonitor = new PortMonitor(mHandler);
        mIsRunning = false;
        mReceiver = new ConnectionChangeReceiver();

        MyApp.getContext().registerReceiver(mReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void onResume(){
        if(mIsRunning){
            return;
        }
        mViewLayer.pageLoading(true);

        final boolean isWifiEnable = WiFiModule.getInstance().isEnable();

        if(isWifiEnable){
            mState = WIFI_READY;
            mViewLayer.wifiReadyNow();
            checkMonitorState();
        }else {
            mState = WIFI_NO_READY;
            mViewLayer.wifiNotReady();
        }
    }

    public void toggleMonitorState(){
        if(mState == MONITORING){
            changeMonitorState(NO_MONITORING);
        }else if(mState == NO_MONITORING){
            changeMonitorState(MONITORING);
        }
    }

    private void changeMonitorState(@STATE int newState){
        if(!canRun()){
            return;
        }

        if (newState == NO_MONITORING){
            mIsRunning = mMonitor.stop();
        }else if(newState == MONITORING){
            mIsRunning = mMonitor.start();
        }

        mViewLayer.pageLoading(mIsRunning);
    }

    private void checkMonitorState(){
        if (!canRun()){
            return;
        }

        mIsRunning = mMonitor.check();
        mViewLayer.pageLoading(mIsRunning);
    }

    private boolean canRun(){
        return !mIsRunning && mState != WIFI_NO_READY;
    }

    public void onDestroy(){
        MyApp.getContext().unregisterReceiver(mReceiver);
        mReceiver = null;
        mMonitor.onDestroy();
    }

    private class MyHandler extends Handler{
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void dispatchMessage(Message msg) {
            final int what = msg.what;

            switch (what){
                case PortMonitor.MONITOR_ENABLE:
                    onMonitorEnable();
                    break;

                case PortMonitor.MONITOR_DISABLE:
                    onMonitorDisable();
                    break;

                default:
                    break;
            }

            super.dispatchMessage(msg);
        }
    }

    private void onMonitorEnable() {
        mState = MONITORING;
        mViewLayer.monitorEnable(WiFiModule.getInstance().getIp());
        mViewLayer.pageLoading(false);
        mIsRunning = false;

    }

    private void onMonitorDisable(){
        mState = NO_MONITORING;
        mViewLayer.monitorDisable();
        mViewLayer.pageLoading(false);
        mIsRunning = false;
    }

    public class ConnectionChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean wifiEnable = WiFiModule.getInstance().isEnable();

            if(!wifiEnable){
                changeMonitorState(NO_MONITORING);
            }
        }
    }

    public interface MainView{
        void pageLoading(boolean display);
        void wifiNotReady();
        void wifiReadyNow();
        void monitorEnable(String ip);
        void monitorDisable();
    }
}
