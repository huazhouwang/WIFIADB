package adb.wifi.woaiwhz.wifiadbandroid.base;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public class CommandExecutor {

    @WorkerThread
    public static @NonNull MonitorResult execute(final boolean needRoot, @NonNull final String[] commands) {
        Process process = null;
        DataOutputStream output2Process = null;
        BufferedReader successReader = null;
        BufferedReader errorReader = null;
        MonitorResult result;

        try {
            final String user;

            if(needRoot){
                user = Config.SU;
            }else {
                user = Config.SH;
            }

            process = Runtime.getRuntime().exec(user);
            output2Process = new DataOutputStream(process.getOutputStream());

            Thread.sleep(100);
            for(String command : commands){
                if(!TextUtils.isEmpty(command)) {
                    output2Process.writeBytes(command + Config.END_LINE);
                }
            }

            output2Process.writeBytes(Config.EXIT + Config.END_LINE);
            output2Process.flush();
            process.waitFor();

            successReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            final StringBuilder successBuilder = new StringBuilder();
            final StringBuilder errorBuilder = new StringBuilder();
            String line;

            while (!TextUtils.isEmpty(line = successReader.readLine())){
                successBuilder.append(line);
            }

            while (!TextUtils.isEmpty(line = errorReader.readLine())){
                errorBuilder.append(line);
            }

            if(!TextUtils.isEmpty(line = errorBuilder.toString())){
                result = new MonitorResult(false,line);
            }else {
                line = successBuilder.toString();
                result = new MonitorResult(true,line);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result = new MonitorResult(false,e.getMessage());

            return result;
        } finally {
            try {
                if (output2Process != null) {
                    output2Process.close();
                }

                if(successReader != null){
                    successReader.close();
                }

                if(errorReader != null){
                    errorReader.close();
                }

                if(process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
