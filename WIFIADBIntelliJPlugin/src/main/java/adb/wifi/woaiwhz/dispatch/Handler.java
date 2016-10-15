package adb.wifi.woaiwhz.dispatch;

import org.jetbrains.annotations.NotNull;

/**
 * Created by huazhou.whz on 2016/10/8.
 */
public abstract class Handler {
    protected abstract void handleMessage(@NotNull Message msg);

    public void sendEmptyMessage(int what){
        final Message msg = new Message(what);

        sendMessage(msg);
    }

    public void sendMessage(@NotNull final Message msg){
        post(new Runnable() {
            @Override
            public void run() {
                handleMessage(msg);
            }
        });
    }

    public void sendMessage(int what,Object obj){
        sendMessage(new Message(what,obj));
    }

    public void post(Runnable runnable){
        Executor.post(runnable);
    }
}
