package adb.wifi.woaiwhz.command;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Notify;
import adb.wifi.woaiwhz.base.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public class AddDevice implements ICommand<String,String> {
    private static final String CANNOT = "cannot";
    private static final String UNABLE = "unable";

    private final String mDeviceId;

    public AddDevice(@NotNull String deviceId){
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
                final String line = lines[0];

                if (Utils.isBlank(line)){
                    return null;
                }

                if (line.contains(CANNOT) || line.contains(UNABLE)){
                    return Utils.concat("Cannot connect to ",mDeviceId);
                }else if (line.contains(Utils.concat("connected to ",mDeviceId))){
                    return Utils.concat("Connected to ",mDeviceId);
                }
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
