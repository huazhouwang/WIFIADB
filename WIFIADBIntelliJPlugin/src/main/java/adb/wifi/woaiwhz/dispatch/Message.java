package adb.wifi.woaiwhz.dispatch;

/**
 * Created by huazhou.whz on 2016/10/8.
 */
public class Message {
    public final Object tag;
    public final int what;

    public Message(int what){
        this.what = what;
        tag = null;
    }

    public Message(Object tag, int what) {
        this.tag = tag;
        this.what = what;
    }
}
