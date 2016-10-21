package adb.wifi.woaiwhz.command;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Notify;
import adb.wifi.woaiwhz.base.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public class ConnectDevice implements ICommand<String,String> {
    private static final String CANNOT = "cannot";
    private static final String UNABLE = "unable";

    private final String mDeviceId;

    public ConnectDevice(@NotNull String deviceId){
        mDeviceId = deviceId;
    }

    @Override
    public String parse(String s) {
        if (Utils.isBlank(s)){
            return null;
        }

        try {
            final String[] lines = Utils.removeDaemon(s.split(Config.ENTER));

            if (lines.length > 0){
                return lines[0];
            }

        }catch (Exception e){
            final String track = e.getMessage();
            Notify.error(track);
        }

        return null;
    }

    @Override
    public String getCommand(@NotNull String adbPath) {
        return Utils.concat(adbPath,Config.SPACE,CONNECT_DEVICE,Config.SPACE,mDeviceId);
    }
}
