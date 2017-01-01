package adb.wifi.woaiwhz.command;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by huazhou.whz on 2016/10/21.
 */
public class SpecifyPort implements ICommand<String,Void> {
    private String mDeviceId;

    public SpecifyPort(@NotNull String deviceId){
        mDeviceId = deviceId;
    }

    @Override
    public Void parse(String s) {
        return null;
    }

    @Override
    public String getCommand(@NotNull String adbPath) {
        return Utils.concat(adbPath, Config.SPACE,TARGET,Config.SPACE,mDeviceId,
                Config.SPACE,ALERT_PORT,Config.SPACE,Config.DEFAULT_PORT);
    }
}
