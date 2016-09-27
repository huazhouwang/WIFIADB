package adb.wifi.woaiwhz.base;

import org.apache.http.util.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by huazhou.whz on 2016/9/27.
 */
public class CommandExecute {

    public static String execute(final String command) {
        Process process = null;
        BufferedReader successReader = null;
        BufferedReader errorReader = null;
        String result = null;

        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();

            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            final StringBuilder successBuilder = new StringBuilder();
            final StringBuilder errorBuilder = new StringBuilder();
            String line;

            while (!TextUtils.isEmpty(line = successReader.readLine())) {
                successBuilder.append(line);
            }

            while (!TextUtils.isEmpty(line = errorReader.readLine())) {
                errorBuilder.append(line);
            }

            if (!TextUtils.isEmpty(line = errorBuilder.toString())) {
                result = line;
            } else {
                line = successBuilder.toString();
                result = line;
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        } finally {
            try {

                if (successReader != null) {
                    successReader.close();
                }

                if (errorReader != null) {
                    errorReader.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
