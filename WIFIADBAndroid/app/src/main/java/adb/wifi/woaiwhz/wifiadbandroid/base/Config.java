package adb.wifi.woaiwhz.wifiadbandroid.base;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public interface Config {
    int PORT = 5555;

    String SH = "sh";
    String SU = "su";
    String END_LINE = "\n";
    String EXIT = "exit";

    String[] CHECK_MONITOR = new String[]{
            "getprop service.adb.tcp.port"
    };

    String[] START_MONITOR = new String[]{
            "setprop service.adb.tcp.port " + PORT,
            "stop adbd",
            "start adbd"
    };

    String[] STOP_MONITOR = new String[]{
            "setprop service.adb.tcp.port -1"
    };
}
