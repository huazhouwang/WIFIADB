package adb.wifi.woaiwhz.base;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by huazhou.whz on 2016/10/14.
 */
public class Utils {
    private static Cursor mHandCursor;

    public static Cursor getHandCursor(){
        if (mHandCursor == null){
            mHandCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }

        return mHandCursor;
    }

    public static String concat(String... texts){
        final StringBuilder builder = new StringBuilder();

        for (final String text : texts){
            builder.append(text);
        }

        return builder.toString();
    }

    public static boolean isRemoteDevice(String deviceId){
        return !isBlank(deviceId) && Pattern.matches(Config.IP_PATTERN,deviceId);
    }

    public static boolean isBlank(String text){
        return StringUtil.isEmptyOrSpaces(text);
    }

    public static String getAdbPath(@NotNull Project project) {
        String adbPath = "";
        final File adbFile = AndroidSdkUtils.getAdb(project);

        if (adbFile != null) {
            adbPath = adbFile.getAbsolutePath();
        }

        return adbPath;
    }

    public static String[] removeDaemon(@NotNull String[] strings){
        final List<String> list = new ArrayList<String>();

        for (final String line : strings){
            if(line.contains(Config.DAEMON_FLAG)){
                continue;
            }

            list.add(line);
        }

        final String[] newStrings = new String[list.size()];

        return list.toArray(newStrings);
    }
}
