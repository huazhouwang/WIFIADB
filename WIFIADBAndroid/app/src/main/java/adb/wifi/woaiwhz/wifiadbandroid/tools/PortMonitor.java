package adb.wifi.woaiwhz.wifiadbandroid.tools;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import adb.wifi.woaiwhz.wifiadbandroid.BuildConfig;
import adb.wifi.woaiwhz.wifiadbandroid.base.CommandExecutor;
import adb.wifi.woaiwhz.wifiadbandroid.base.Config;
import adb.wifi.woaiwhz.wifiadbandroid.base.MonitorResult;

/**
 * Created by huazhou.whz on 2016/9/13.
 */
public class PortMonitor {
    private static final String TAG = PortMonitor.class.getSimpleName();
    public static final int PORT_READY_NOW = 1;
    public static final int PORT_NO_READY_NOW = 1 << 1;

    private Handler mHandler;
    private ExecutorService mExecutor;
    private AtomicInteger mCurrent;
    private int mAssignIndex;

    public PortMonitor(@NonNull Handler handler){
        mHandler = handler;
        mAssignIndex = 0;
        mCurrent = new AtomicInteger(mAssignIndex);
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public boolean checkPort(){
        if(mAssignIndex == mCurrent.get()){
            mExecutor.execute(new CheckMonitor(mHandler, mCurrent));
            ++mAssignIndex;
            return true;
        }else {
            return false;
        }
    }

    public boolean stopPort(){
        if(mAssignIndex == mCurrent.get()){
            mExecutor.execute(new ExecuteMonitor(mHandler, false, mCurrent));
            ++mAssignIndex;
            return true;
        }else {
            return false;
        }
    }

    public boolean startPort(){
        if(mAssignIndex == mCurrent.get()){
            mExecutor.execute(new ExecuteMonitor(mHandler, true, mCurrent));
            ++mAssignIndex;
            return true;
        }else {
            return false;
        }
    }

    private static class ExecuteMonitor implements Runnable{
        private final boolean m2Enable;
        private final WeakReference<Handler> mReference;
        private final AtomicInteger mCurrent;
        private final int mAssignIndex;
        private final String[] mCommands;

        private ExecuteMonitor(@NonNull Handler handle,final boolean enable,AtomicInteger current){
            mReference = new WeakReference<>(handle);
            m2Enable = enable;

            if(enable){
                mCommands = Config.START_MONITOR;
            }else {
                mCommands = Config.STOP_MONITOR;
            }

            mCurrent = current;
            mAssignIndex = mCurrent.get();
        }

        @Override
        public void run() {
            final MonitorResult result = CommandExecutor.execute(true,mCommands);

            if(mCurrent.compareAndSet(mAssignIndex,mAssignIndex + 1)) {
                if ((result.success && m2Enable)
                        || (!result.success && !m2Enable)) {
                    isEnable(result);
                } else {
                    isDisable(result);
                }
            }
        }

        private void isEnable(@NonNull MonitorResult result){
            final Handler handler = mReference.get();

            if(handler != null){
                handler.sendEmptyMessage(PORT_READY_NOW);
            }
        }

        private void isDisable(@NonNull MonitorResult result){
            final Handler handler = mReference.get();

            if(handler != null){
                handler.sendEmptyMessage(PORT_NO_READY_NOW);
            }
        }
    }

    private static class CheckMonitor implements Runnable{
        private WeakReference<Handler> mReference;
        private final AtomicInteger mCurrent;
        private final int mAssignIndex;

        private CheckMonitor(@NonNull Handler handler,AtomicInteger current){
            mReference = new WeakReference<>(handler);
            mCurrent = current;
            mAssignIndex = current.get();
        }

        @Override
        public void run() {
            final MonitorResult result = CommandExecutor.execute(false, Config.CHECK_MONITOR);

            if(mCurrent.compareAndSet(mAssignIndex,mAssignIndex + 1)) {
                if (result.success && !TextUtils.isEmpty(result.message)) {
                    try {
                        if (Integer.parseInt(result.message) == Config.PORT) {
                            success(result);
                        } else {
                            fail(result);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        fail(result);
                    }
                } else {
                    fail(result);
                }
            }
        }

        private void success(@NonNull MonitorResult result){
            final Handler handler = mReference.get();

            if(handler != null){
                handler.sendEmptyMessage(PORT_READY_NOW);
            }
        }

        private void fail(@NonNull MonitorResult result){
            final Handler handler = mReference.get();

            if(handler != null){
                handler.sendEmptyMessage(PORT_NO_READY_NOW);

                if(BuildConfig.DEBUG){
                    Log.e(TAG, "CheckMonitor::run >> " + result);
                }
            }
        }
    }

    public void interrupt(){
        mAssignIndex = mCurrent.incrementAndGet();

        final int[] messageWhat = new int[]{
                PORT_READY_NOW,
                PORT_NO_READY_NOW
        };

        for (final int what : messageWhat){
            mHandler.removeMessages(what);
        }
    }
}
