package adb.wifi.woaiwhz.base;

/**
 * Created by huazhou.whz on 2016/10/7.
 */
public interface Config {
    String ADB_PATH = "adb_path";
    String TITLE = "Wifi Adb Ultimate";
    String HELP = "https://github.com/Sausure/WIFIADB/tree/master/WIFIADBIntelliJPlugin";
    String DEFAULT_PORT = "5555";
    String EMPTY = "";
    String SPACE = " ";
    String ANY_SPACES = "\\s{1,}";
    String ENTER = "\\n";
    String TAB = "\\t";
    String DEFAULT_PROGRESS_TIP = "Wait a moment";
    String DAEMON_FLAG = Utils.concat(SPACE,"daemon",SPACE);
    String IP_PATTERN = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}";
}
