package adb.wifi.woaiwhz.wifiadbandroid.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import adb.wifi.woaiwhz.wifiadbandroid.base.WiFiModule;
import adb.wifi.woaiwhz.wifiadbandroid.tools.PortMonitor;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public class MainPresenter {
    private static final int MONITORING = 1;
    private static final int NO_MONITORING = 1 << 1;
    private static final int WIFI_DISABLE = 1 << 2;

    @IntDef({MONITORING, NO_MONITORING,WIFI_DISABLE})
    @Retention(SOURCE)
    private @interface STATE{}

    private final Handler mHandler;
    private final PortMonitor mMonitor;
    private final MainView mViewLayer;
    private @STATE int mState;
    private boolean mIsRunning;

    public MainPresenter(MainView viewLayer){
        mViewLayer = viewLayer;
        mHandler = new MyHandler(Looper.getMainLooper());
        mMonitor = new PortMonitor(mHandler);
        mIsRunning = false;

        init();
    }

    private void init(){
        final boolean isWifiEnable = WiFiModule.getInstance().isEnable();

        if(isWifiEnable){
            // TODO: 2016/9/14
        }else {
            mViewLayer.wifiNotReady();
        }
    }

    public void changeMonitorState(){
        if(mIsRunning){
            return;
        }

        if(mState == NO_MONITORING){
            mIsRunning = true;
            mIsRunning = mMonitor.start();
        }else if(mState == MONITORING){
            mIsRunning = true;
            mIsRunning = mMonitor.stop();
        }
    }

    public void checkMonitorState(){
        if (mIsRunning || mState == WIFI_DISABLE){
            return;
        }

        mIsRunning = mMonitor.check();
    }

    public void onDestroy(){
        mMonitor.onDestroy();
    }

    private class MyHandler extends Handler{
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    }

    public interface MainView{
        void wifiNotReady();
    }
}
