package adb.wifi.woaiwhz.command;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Notify;
import adb.wifi.woaiwhz.base.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by huazhou.whz on 2016/10/21.
 */
public class DisconnectRemoteDevice implements ICommand<String,String> {
    private String mDeviceId;

    public DisconnectRemoteDevice(@NotNull String deviceId){
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
            e.printStackTrace();
            Notify.error(e.getMessage());
        }

        return null;
    }

    @Override
    public String getCommand(@NotNull String adbPath) {
        return Utils.concat(adbPath, Config.SPACE,DISCONNECT_DEVICE,Config.SPACE, mDeviceId);
    }
}
