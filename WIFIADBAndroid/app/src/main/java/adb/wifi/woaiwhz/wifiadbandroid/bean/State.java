package adb.wifi.woaiwhz.wifiadbandroid.bean;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by huazhou.whz on 2016/9/17.
 */
public interface State {
    int INIT = 0;
    int WIFI_UNREADY = 1;
    int WIFI_READY = 1 << 1;
    int PORT_UNREADY = 1 << 2;
    int PORT_READY = 1 << 3;

    @IntDef({INIT,PORT_READY, PORT_UNREADY, WIFI_UNREADY,WIFI_READY})
    @Retention(SOURCE)
    @interface STATE{}
}
