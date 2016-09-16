package adb.wifi.woaiwhz.wifiadbandroid.tools;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private AtomicBoolean mFree;

    public PortMonitor(@NonNull Handler handler){
        mHandler = handler;
        mFree = new AtomicBoolean(true);
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public boolean checkPort(){
        if(mFree.compareAndSet(true,false)) {
            mExecutor.execute(new CheckMonitor(mHandler,mFree));
            return true;
        }else {
            return false;
        }
    }

    public boolean stopPort(){
        if(mFree.compareAndSet(true,false)) {
            mExecutor.execute(new ExecuteMonitor(mHandler, false, mFree));
            return true;
        }else {
            return false;
        }
    }

    public boolean startPort(){
        if(mFree.compareAndSet(true,false)) {
            mExecutor.execute(new ExecuteMonitor(mHandler, true,mFree));
            return true;
        }else {
            return false;
        }
    }

    private static class ExecuteMonitor implements Runnable{
        private final boolean m2Enable;
        private final WeakReference<Handler> mReference;
        private final AtomicBoolean mFree;
        private final String[] mCommands;

        private ExecuteMonitor(@NonNull Handler handle,final boolean enable,AtomicBoolean free){
            mReference = new WeakReference<>(handle);
            m2Enable = enable;

            if(enable){
                mCommands = Config.START_MONITOR;
            }else {
                mCommands = Config.STOP_MONITOR;
            }

            mFree = free;
        }

        @Override
        public void run() {
            final MonitorResult result = CommandExecutor.execute(true,mCommands);

            if((result.success && m2Enable)
                    || (!result.success && !m2Enable)){
                isEnable(result);
            }else {
                isDisable(result);
            }
        }

        private void isEnable(@NonNull MonitorResult result){
            final Handler handler = mReference.get();

            if(handler != null){
                handler.sendEmptyMessage(PORT_READY_NOW);
                mFree.set(true);
            }//if handler is null,then let mFree stay in false
        }

        private void isDisable(@NonNull MonitorResult result){
            final Handler handler = mReference.get();

            if(handler != null){
                handler.sendEmptyMessage(PORT_NO_READY_NOW);
                mFree.set(true);
            }
        }
    }

    private static class CheckMonitor implements Runnable{
        private WeakReference<Handler> mReference;
        private final AtomicBoolean mFree;

        private CheckMonitor(@NonNull Handler handler,AtomicBoolean free){
            mReference = new WeakReference<>(handler);
            mFree = free;
        }

        @Override
        public void run() {
            final MonitorResult result = CommandExecutor.execute(false, Config.CHECK_MONITOR);

            if (result.success && !TextUtils.isEmpty(result.message)){
                try{
                    if(Integer.parseInt(result.message) == Config.PORT){
                        success(result);
                    }else {
                        fail(result);
                    }
                }catch (Exception e){
                    if(BuildConfig.DEBUG){
                        e.printStackTrace();
                    }
                    fail(result);
                }
            }else {
                fail(result);
            }
        }

        private void success(@NonNull MonitorResult result){
            final Handler handler = mReference.get();

            if(handler != null){
                handler.sendEmptyMessage(PORT_READY_NOW);
                mFree.set(true);
            }
        }

        private void fail(@NonNull MonitorResult result){
            final Handler handler = mReference.get();

            if(handler != null){
                handler.sendEmptyMessage(PORT_NO_READY_NOW);
                mFree.set(true);

                if(BuildConfig.DEBUG){
                    Log.e(TAG, "CheckMonitor::run >> " + result);
                }
            }
        }
    }

    public void interrupt(){
        final int[] messageWhat = new int[]{
                PORT_READY_NOW,
                PORT_NO_READY_NOW
        };

        for (final int what : messageWhat){
            mHandler.removeMessages(what);
        }
    }
}
