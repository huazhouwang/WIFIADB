package adb.wifi.woaiwhz.wifiadbandroid;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.io.DataOutputStream;

/**
 * Created by huazhou.whz on 2016/9/13.
 */
public class PortMonitor {
    private static final String TAG = PortMonitor.class.getSimpleName();

    private static final int PORT = 5555;

    private static final String[] START_MONITOR = new String[]{
            "setprop service.adb.tcp.port " + PORT,
            "stop adbd",
            "start adbd"
    };

    private static final String[] STOP_MONITOR = new String[]{
            "setprop service.adb.tcp.port -1",
            "stop adbd",
            "start adbd"
    };

    private Handler mHandler;

    private boolean mRunning;

    public PortMonitor(@NonNull Handler handler){
        mHandler = handler;
        mRunning = false;
    }

    public void stop(){
        execute(STOP_MONITOR);
    }

    public void start(){
        execute(START_MONITOR);
    }

    private void execute(@NonNull final String[] commands){
        if(mRunning){
            return;
        }

        mRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(rootCommand(commands)){
                    mHandler.sendEmptyMessage(0);
                }else {
                    mHandler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    private static boolean rootCommand(@NonNull final String[] commands)
    {
        final String GO = "\n";

        Process process = null;
        DataOutputStream os = null;

        try
        {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());

            for(String command : commands){
                os.writeBytes(command + GO);
            }

            os.writeBytes("exit" + GO);
            os.flush();
            process.waitFor();

        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        } finally {
            try
            {
                if (os != null)
                {
                    os.close();
                }

                if(process != null) {
                    process.destroy();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return true;
    }
}
