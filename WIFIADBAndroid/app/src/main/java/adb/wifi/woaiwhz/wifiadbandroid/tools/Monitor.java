package adb.wifi.woaiwhz.wifiadbandroid.tools;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

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
public class Monitor {
    private static final String TAG = Monitor.class.getSimpleName();

    public static final int ACTION_FAIL = 1;
    public static final int ACTION_READY_PORT_SUCCESS = 1 << 1;
    public static final int ACTION_UNREADY_PORT_SUCCESS = 1 << 2;

    private final Handler mHandler;
    private final ExecutorService mExecutor;
    private final AtomicInteger mCurrent;

    private int mAssignIndex;

    public Monitor(@NonNull Handler handler){
        mHandler = handler;
        mAssignIndex = 0;
        mCurrent = new AtomicInteger(mAssignIndex);
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public boolean checkPort(){
        final int index = mCurrent.get();

        if(mAssignIndex == index){
            mExecutor.execute(new CheckMonitor(index));
            ++mAssignIndex;
            return true;
        }else {
            return false;
        }
    }

    public boolean try2monitor(){
        final int index = mCurrent.get();

        if(mAssignIndex == index){
            mExecutor.execute(new ExecuteMonitor(true,index));
            ++mAssignIndex;
            return true;
        }else {
            return false;
        }
    }

    public boolean cancelMonitor(){
        final int index = mCurrent.get();

        if(mAssignIndex == index){
            mExecutor.execute(new ExecuteMonitor(false,index));
            ++mAssignIndex;
            return true;
        }else {
            return false;
        }
    }

    private class ExecuteMonitor implements Runnable{
        private final boolean mIsReady;
        private final int mAssignIndex;
        private final String[] mCommands;

        private ExecuteMonitor(final boolean enable,int assignIndex){
            mIsReady = enable;

            if(enable){
                mCommands = Config.START_MONITOR;
            }else {
                mCommands = Config.STOP_MONITOR;
            }

            mAssignIndex = assignIndex;
        }

        @Override
        public void run() {
            final MonitorResult result = CommandExecutor.execute(true,mCommands);

            if(result.success){
                final Runnable runnable = new CheckMonitor(mAssignIndex);
                runnable.run();
            }else {
                mCurrent.compareAndSet(mAssignIndex,mAssignIndex + 1);
                fail(result);
            }

        }
    }

    private class CheckMonitor implements Runnable{
        private final int mAssignIndex;

        private CheckMonitor(int assignIndex){
            mAssignIndex = assignIndex;
        }

        @Override
        public void run() {
            final MonitorResult result = CommandExecutor.execute(false, Config.CHECK_MONITOR);

            if(mCurrent.compareAndSet(mAssignIndex,mAssignIndex + 1)) {
                if (result.success) {
                    try {
                        if(TextUtils.isEmpty(result.message) || Integer.parseInt(result.message) != Config.PORT){
                            portUnReady(result);
                        }else {
                            portReady(result);
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
    }

    private void portReady(@NonNull MonitorResult result){
        final Message message = new Message();
        message.what = ACTION_READY_PORT_SUCCESS;
        message.obj = result;
        mHandler.sendMessage(message);

        if(BuildConfig.DEBUG){
            Log.i(TAG, "portReady: " + result);
        }
    }

    private void portUnReady(@NonNull MonitorResult result){
        final Message message = new Message();
        message.what = ACTION_UNREADY_PORT_SUCCESS;
        message.obj = result;
        mHandler.sendMessage(message);

        if(BuildConfig.DEBUG){
            Log.i(TAG, "portUnReady: " + result);
        }
    }

    private void fail(@NonNull MonitorResult result){
        final Message message = new Message();
        message.what = ACTION_FAIL;
        message.obj = result;
        mHandler.sendMessage(message);

        if(BuildConfig.DEBUG){
            Log.i(TAG, "fail: " + result);
        }
    }

    public void interrupt(){
        mAssignIndex = mCurrent.incrementAndGet();

        final int[] messageWhat = new int[]{
                ACTION_FAIL,
                ACTION_READY_PORT_SUCCESS,
                ACTION_UNREADY_PORT_SUCCESS
        };

        for (final int what : messageWhat){
            mHandler.removeMessages(what);
        }
    }
}
