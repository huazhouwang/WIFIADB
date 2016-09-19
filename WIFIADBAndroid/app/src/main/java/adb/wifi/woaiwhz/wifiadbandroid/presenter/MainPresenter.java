package adb.wifi.woaiwhz.wifiadbandroid.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import adb.wifi.woaiwhz.wifiadbandroid.MyApp;
import adb.wifi.woaiwhz.wifiadbandroid.base.State;
import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;
import adb.wifi.woaiwhz.wifiadbandroid.tools.Monitor;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public class MainPresenter {
    private final Monitor mMonitor;
    private final MainView mViewLayer;
    private final BroadcastReceiver mReceiver;

    @State.STATE
    private int mState;
    private boolean mRunning;

    public MainPresenter(MainView viewLayer){
        mViewLayer = viewLayer;
        final Handler handler = new MyHandler(Looper.getMainLooper());
        mMonitor = new Monitor(handler);
        mReceiver = new WifiChangeReceiver();

        mRunning = false;
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

    private void wifiReady(){
        if(mState != State.WIFI_READY) {
            mState = State.WIFI_READY;
            mViewLayer.onWifiReady();
            checkPortState();
        }
    }

    private void wifiNoReady(){
        if(mState != State.WIFI_UNREADY) {
            mState = State.WIFI_UNREADY;
            mViewLayer.onWifiUnready();
        }
    }

    public void togglePortState(){
        if(mState == State.PORT_READY){
            changePort(State.PORT_UNREADY);
        }else if(mState == State.PORT_UNREADY){
            changePort(State.PORT_READY);
        }
    }

    private void changePort(@State.STATE int newState){
        if(cannotRun()){
            return;
        }

        if (newState == State.PORT_UNREADY){
            mRunning = mMonitor.cancelMonitor();
        }else if(newState == State.PORT_READY){
            mRunning = mMonitor.try2monitor();
        }

        if(mRunning){
            mViewLayer.pageLoading(true);
        }
    }

    private void checkPortState(){
        if (cannotRun()){
            return;
        }

        mRunning = mMonitor.checkPort();

        if(mRunning) {
            mViewLayer.pageLoading(true);
        }
    }

    private boolean cannotRun(){
        return mRunning || mState < State.WIFI_READY;
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
                case Monitor.ACTION_READY_PORT_SUCCESS:
                    onPortReady();
                    break;

                case Monitor.ACTION_UNREADY_PORT_SUCCESS:
                    onPortUnready();
                    break;

                case Monitor.ACTION_FAIL:
                    final String message = (String) msg.obj;
                    onFail(message);
                    break;

                default:
                    break;
            }

            mViewLayer.pageLoading(false);
            mRunning = false;
        }
    }

    private void onPortReady() {
        if(mState != State.PORT_READY) {
            mState = State.PORT_READY;
            mViewLayer.onPortReady(WiFiModule.getInstance().getIp());
        }
    }

    private void onPortUnready(){
        if(mState != State.PORT_UNREADY) {
            mState = State.PORT_UNREADY;
            mViewLayer.onPortUnready();
        }
    }

    private void onFail(String message){
        if(TextUtils.isEmpty(message)){
            message = "Something wrong";
        }

        mViewLayer.onActionFail(message);
    }

    private class WifiChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean wifiReady = WiFiModule.getInstance().isReady();
            if(wifiReady){
                if(mState < State.WIFI_READY){
                    wifiReady();
                }
            }else if(mState >= State.WIFI_UNREADY){
                wifiNoReady();
                mViewLayer.pageLoading(false);
                mMonitor.interrupt();
                mRunning = false;
            }
        }
    }

    public interface MainView{
        void pageLoading(boolean show);
        void onWifiUnready();
        void onWifiReady();
        void onPortReady(String ip);
        void onPortUnready();
        void onActionFail(@NonNull String message);
    }
}
