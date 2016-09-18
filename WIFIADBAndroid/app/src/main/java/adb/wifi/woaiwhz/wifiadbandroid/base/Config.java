package adb.wifi.woaiwhz.wifiadbandroid.base;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public interface Config {
    int PORT = 5555;
    int EOF_PORT = -1;

    String SH = "sh";
    String SU = "su";
    String END_LINE = "\n";
    String EXIT = "exit";
    String TARGET = "service.adb.tcp.port";
    String GET = "getprop";
    String SET = "setprop";
    String SPACE = " ";

    String[] CHECK_MONITOR = new String[]{
            GET + SPACE + TARGET
    };

    String[] START_MONITOR = new String[]{
            SET + SPACE + TARGET + SPACE + PORT,
    };

    String[] STOP_MONITOR = new String[]{
            SET + SPACE + TARGET + SPACE + EOF_PORT
    };
}
