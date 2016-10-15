package adb.wifi.woaiwhz.dispatch;

import org.jetbrains.annotations.Nullable;

/**
 * Created by huazhou.whz on 2016/10/8.
 */
public class Message {
    public final Object obj;
    public final int what;

    public Message(int what){
        this.what = what;
        obj = null;
    }

    public Message(int what,@Nullable Object obj) {
        this.what = what;
        this.obj = obj;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(){
        if(obj == null){
            return null;
        }

        try{
            return (T) obj;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
