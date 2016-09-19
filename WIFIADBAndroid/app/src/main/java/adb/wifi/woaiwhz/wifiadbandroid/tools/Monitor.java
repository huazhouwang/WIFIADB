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
        private final boolean mMakeReady;
        private final int mAssignIndex;
        private final String[] mCommands;

        private ExecuteMonitor(final boolean makeReady,int assignIndex){
            mMakeReady = makeReady;

            if(makeReady){
                mCommands = Config.START_MONITOR;
            }else {
                mCommands = Config.STOP_MONITOR;
            }

            mAssignIndex = assignIndex;
        }

        @Override
        public void run() {
            synchronized (mHandler) {
                if (!mCurrent.compareAndSet(mAssignIndex, mAssignIndex + 1)) {
                    return;
                }

                final MonitorResult result = CommandExecutor.execute(true, mCommands);
                if (result.success) {
                    final MonitorResult result2 = CommandExecutor.execute(false, Config.CHECK_MONITOR);
                    if (result2.success) {
                        if (mMakeReady) {
                            if (verifyPort(result2, Config.PORT)) {
                                portReady();
                                return;
                            }
                        } else if (verifyPort(result2, Config.EOF_PORT)) {
                            portUnReady();
                            return;
                        }

                        fail("fail to gain root authority");
                        return;
                    }
                }

                fail("fail to execute");
            }
        }
    }
    private boolean verifyPort(@NonNull MonitorResult result, int target){
        final String current = result.message;

        if(target == Config.PORT) {
            try {
                return !TextUtils.isEmpty(current) && Integer.parseInt(current) == target;
            } catch (NumberFormatException e) {
                return false;
            }
        }else if(target == Config.EOF_PORT){
            try {
                return TextUtils.isEmpty(current) || Integer.parseInt(current) == target;
            }catch (NumberFormatException e) {
                return true;
            }
        }

        throw new IllegalArgumentException();
    }

    private class CheckMonitor implements Runnable{
        private final int mAssignIndex;

        private CheckMonitor(int assignIndex){
            mAssignIndex = assignIndex;
        }

        @Override
        public void run() {
            synchronized (mHandler) {
                if (!mCurrent.compareAndSet(mAssignIndex, mAssignIndex + 1)) {
                    return;
                }

                final MonitorResult result = CommandExecutor.execute(false, Config.CHECK_MONITOR);

                if (result.success) {
                    if (verifyPort(result, Config.PORT)) {
                        portReady();
                    } else {
                        portUnReady();
                    }
                } else {
                    fail("fail");
                }
            }
        }
    }

    private void portReady(){
        mHandler.sendEmptyMessage(ACTION_READY_PORT_SUCCESS);
    }

    private void portUnReady(){
        mHandler.sendEmptyMessage(ACTION_UNREADY_PORT_SUCCESS);
    }

    private void fail(String data){
        final Message message = new Message();
        message.what = ACTION_FAIL;
        message.obj = data;
        mHandler.sendMessage(message);

        if(BuildConfig.DEBUG){
            Log.i(TAG, "fail: " + data);
        }
    }

    public void interrupt() {
        synchronized (mHandler) {
            mAssignIndex = mCurrent.incrementAndGet();
            final int[] messageWhat = new int[]{
                    ACTION_FAIL,
                    ACTION_READY_PORT_SUCCESS,
                    ACTION_UNREADY_PORT_SUCCESS
            };

            for (final int what : messageWhat) {
                mHandler.removeMessages(what);
            }
        }
    }
}
