package adb.wifi.woaiwhz.parser;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Notify;
import adb.wifi.woaiwhz.base.Utils;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public class ConnectDevice implements ICommand<String,Boolean> {
    private static final String CANNOT = "cannot";
    private static final String UNABLE = "unable";

    @Override
    public Boolean parse(String s) {
        if (Utils.isBlank(s)){
            return false;
        }

        try {
            final String[] lines = Utils.removeDaemon(s.split(Config.ENTER));

            for (final String line : lines){
                if(line.contains(CANNOT) || line.contains(UNABLE)){
                    return false;
                }
            }

            return true;
        }catch (Exception e){
            final String track = e.getMessage();
            Notify.error(track);

            return false;
        }
    }

    @Override
    public String getCommand() {
        return CONNECT_DEVICE;
    }
}
