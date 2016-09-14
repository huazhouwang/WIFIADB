package adb.wifi.woaiwhz.wifiadbandroid.tools;

import android.os.Handler;
import android.support.annotation.NonNull;
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
    public static final int FAIL_2_START_MONITOR = 1;
    public static final int SUCCESS_2_START_MONITOR = 1 << 1;
    public static final int SUCCESS_2_CHECK_MONITOR = 1 << 2;
    public static final int SOME_THING_FAIL = 1 << 3;

    private Handler mHandler;
    private ExecutorService mExecutor;

    private AtomicBoolean mFree;

    public PortMonitor(@NonNull Handler handler){
        mHandler = handler;
        mFree = new AtomicBoolean(true);
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public boolean check(){
        if(mFree.compareAndSet(true,false)) {
            mExecutor.execute(new CheckMonitor(mHandler,mFree));
            return true;
        }else {
            return false;
        }
    }

    public boolean stop(){
        if(mFree.compareAndSet(true,false)) {
            mExecutor.execute(new ExecuteMonitor(mHandler, false, mFree));
            return true;
        }else {
            return false;
        }
    }

    public boolean start(){
        if(mFree.compareAndSet(true,false)) {
            mExecutor.execute(new ExecuteMonitor(mHandler, true,mFree));
            return true;
        }else {
            return false;
        }
    }

    private static class ExecuteMonitor implements Runnable{
        private static final int MAX_TRY = 2;

        private final boolean mEnable;
        private final WeakReference<Handler> mReference;
        private final AtomicBoolean mFree;
        private int mTryCount;
        private final String[] mCommands;

        private ExecuteMonitor(@NonNull Handler handle,final boolean enable,AtomicBoolean free){
            mReference = new WeakReference<>(handle);
            mEnable = enable;

            if(mEnable){
                mCommands = Config.START_MONITOR;
            }else {
                mCommands = Config.STOP_MONITOR;
            }
            mTryCount = 0;

            mFree = free;
        }

        @Override
        public void run() {
            ++mTryCount;

            final MonitorResult result = CommandExecutor.execute(true,mCommands);

            if(result.success){
                final Handler handler = mReference.get();

                if(handler != null){
                    handler.sendEmptyMessage(SUCCESS_2_START_MONITOR);
                    mFree.set(true);
                }//if handler is null,then let mFree stay in false
            }else if(mTryCount <= MAX_TRY){
                run();
            }else {
                final Handler handler = mReference.get();
                if(handler != null) {
                    final Runnable runnable = new CheckMonitor(mReference.get(),mFree);
                    runnable.run();
                }
            }
        }
    }

    private static class CheckMonitor implements Runnable{
        private static final int MAX_TRY = 3;

        private WeakReference<Handler> mReference;
        private int mTryCount;
        private final AtomicBoolean mFree;

        private CheckMonitor(@NonNull Handler handler,AtomicBoolean free){
            mReference = new WeakReference<>(handler);
            mTryCount = 0;
            mFree = free;
        }

        @Override
        public void run() {
            ++mTryCount;

            final MonitorResult result = CommandExecutor.execute(false, Config.CHECK_MONITOR);

            if (result.success){
                final Handler handler = mReference.get();

                if(handler != null){
                    handler.sendEmptyMessage(SUCCESS_2_CHECK_MONITOR);
                    mFree.set(true);
                }
            }else if(mTryCount <= MAX_TRY){
                run();
            }else {
                final Handler handler = mReference.get();

                if(handler != null){
                    handler.sendEmptyMessage(SOME_THING_FAIL);
                    mFree.set(true);

                    if(BuildConfig.DEBUG){
                        Log.e(TAG, "CheckMonitor::run >> " + result);
                    }
                }
            }
        }
    }

    public void onDestroy(){
        mExecutor.shutdown();
        mExecutor = null;

        final int[] messageWhat = new int[]{
                FAIL_2_START_MONITOR,
                SUCCESS_2_START_MONITOR,
                SUCCESS_2_CHECK_MONITOR,
                SOME_THING_FAIL
        };

        for (final int what : messageWhat){
            mHandler.removeMessages(what);
        }

        mHandler = null;
    }
}
