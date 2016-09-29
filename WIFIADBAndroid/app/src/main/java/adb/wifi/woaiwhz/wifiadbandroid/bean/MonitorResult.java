package adb.wifi.woaiwhz.wifiadbandroid.bean;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public class MonitorResult {
    public final boolean success;
    public final String message;

    public MonitorResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public String toString() {
        return "result[ " + (success ? "success" : "fail") + " >> " + message + " ]";
    }
}
