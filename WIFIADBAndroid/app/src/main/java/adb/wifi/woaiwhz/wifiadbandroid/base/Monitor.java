package adb.wifi.woaiwhz.wifiadbandroid.base;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import adb.wifi.woaiwhz.wifiadbandroid.BuildConfig;
import adb.wifi.woaiwhz.wifiadbandroid.bean.Config;
import adb.wifi.woaiwhz.wifiadbandroid.bean.MonitorResult;

/**
 * Created by huazhou.whz on 2016/9/13.
 */
public class Monitor {
    private static final String TAG = Monitor.class.getSimpleName();
    private static final String SOMETHING_WRONG = "Something wrong...";

    public static final int ACTION_FAIL = 1;
    public static final int ACTION_PORT_READY = 1 << 1;
    public static final int ACTION_PORT_UNREADY = 1 << 2;

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

    public boolean switch2Ready(){
        final int index = mCurrent.get();

        if(mAssignIndex == index){
            mExecutor.execute(new ExecuteMonitor(true,index));
            ++mAssignIndex;
            return true;
        }else {
            return false;
        }
    }

    public boolean switch2Unready(){
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

                final MonitorResult setResult = CommandExecutor.execute(true, mCommands);
                if (setResult.success) {
                    final MonitorResult checkResult = CommandExecutor.execute(false, Config.CHECK_MONITOR);
                    if (checkResult.success) {
                        if (mMakeReady) {
                            if (portIsValid(checkResult)) {
                                portReady();
                                return;
                            }
                        } else if (!portIsValid(checkResult)) {
                            portUnReady();
                            return;
                        }
                    }
                }

                fail(SOMETHING_WRONG);
            }
        }
    }

    private boolean portIsValid(@NonNull MonitorResult result){
        final String current = result.message;

        try{
            return !TextUtils.isEmpty(current) && Integer.parseInt(current) == Config.PORT;
        }catch (NumberFormatException e){
            e.printStackTrace();

            return false;
        }
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

                final MonitorResult checkResult = CommandExecutor.execute(false, Config.CHECK_MONITOR);

                if (checkResult.success) {
                    if (portIsValid(checkResult)) {
                        portReady();
                    } else {
                        portUnReady();
                    }
                } else {
                    fail(SOMETHING_WRONG);
                }
            }
        }
    }

    private void portReady(){
        mHandler.sendEmptyMessage(ACTION_PORT_READY);
    }

    private void portUnReady(){
        mHandler.sendEmptyMessage(ACTION_PORT_UNREADY);
    }

    private void fail(@NonNull String data){
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
                    ACTION_PORT_READY,
                    ACTION_PORT_UNREADY
            };

            for (final int what : messageWhat) {
                mHandler.removeMessages(what);
            }
        }
    }
}
