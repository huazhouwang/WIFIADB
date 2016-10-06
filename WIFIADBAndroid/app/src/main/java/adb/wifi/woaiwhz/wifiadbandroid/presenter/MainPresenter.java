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

import adb.wifi.woaiwhz.wifiadbandroid.MyApp;
import adb.wifi.woaiwhz.wifiadbandroid.base.Monitor;
import adb.wifi.woaiwhz.wifiadbandroid.bean.Config;
import adb.wifi.woaiwhz.wifiadbandroid.bean.State;
import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;

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
        mState = State.INIT;
    }

    public void onStart(){
        MyApp.getContext().registerReceiver(mReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void checkNow(){
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
        if(mState < State.WIFI_READY) {
            mState = State.WIFI_READY;
        }

        checkPortState();
    }

    private void wifiNoReady(){
        if(mState != State.WIFI_UNREADY) {
            //若最开始时就是 WIFI_UNREADY 就无需通知
            if(mState != State.INIT) {
                mViewLayer.onWifiUnready();
            }
            mState = State.WIFI_UNREADY;
        }
    }

    public void toggle(){
        if(mState == State.PORT_READY){
            changePort(State.PORT_UNREADY);
        }else if(mState == State.PORT_UNREADY){
            changePort(State.PORT_READY);
        }
    }

    private void changePort(@State.STATE int newState){
        if(mRunning){
            return;
        }

        if (newState == State.PORT_UNREADY){
            mRunning = mMonitor.switch2Unready();
        }else if(newState == State.PORT_READY){
            mRunning = mMonitor.switch2Ready();
        }

        if(mRunning){
            mViewLayer.pageLoading(true);
        }
    }

    private void checkPortState(){
        if (mRunning){
            return;
        }

        mRunning = mMonitor.checkPort();

        if(mRunning) {
            mViewLayer.pageLoading(true);
        }
    }

    public void onStop(){
        mMonitor.interrupt();
        MyApp.getContext().unregisterReceiver(mReceiver);
    }

    private class MyHandler extends Handler{
        private MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void dispatchMessage(Message msg) {
            mRunning = false;
            mViewLayer.pageLoading(false);

            final int what = msg.what;
            switch (what){
                case Monitor.ACTION_PORT_READY:
                    onPortReady();
                    break;

                case Monitor.ACTION_PORT_UNREADY:
                    onPortUnready();
                    break;

                case Monitor.ACTION_FAIL:
                    final String message = (String) msg.obj;
                    onFail(message);
                    break;

                default:
                    break;
            }
        }
    }

    private void onPortReady() {
        if(mState != State.PORT_READY) {
            mState = State.PORT_READY;
            mViewLayer.onPortReady(WiFiModule.getInstance().getIp() + " : " + Config.PORT);
        }
    }

    private void onPortUnready(){
        if(mState != State.PORT_UNREADY) {
            mState = State.PORT_UNREADY;
            mViewLayer.onPortUnready();
        }
    }

    private void onFail(String message){
        if(mState == State.WIFI_READY){
            mState = State.INIT;
            onStart();
        }

        mViewLayer.onActionFail(message);
    }

    private class WifiChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNow();
        }
    }

    public interface MainView{
        void pageLoading(boolean show);
        void onWifiUnready();
        void onPortReady(String ip);
        void onPortUnready();
        void onActionFail(@NonNull String message);
    }
}
