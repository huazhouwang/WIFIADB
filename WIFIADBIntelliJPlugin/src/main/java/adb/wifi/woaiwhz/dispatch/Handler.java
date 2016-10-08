package adb.wifi.woaiwhz.dispatch;

/**
 * Created by huazhou.whz on 2016/10/8.
 */
public abstract class Handler {
    protected abstract void handleMessage(Message msg);

    public void sendEmptyMessage(int what){
        final Message msg = new Message(what);

        sendMessage(msg);
    }

    public void sendMessage(final Message msg){
        post(new Runnable() {
            @Override
            public void run() {
                handleMessage(msg);
            }
        });
    }

    public void post(Runnable runnable){
        Executor.post(runnable);
    }
}
