package adb.wifi.woaiwhz.command;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Notify;
import adb.wifi.woaiwhz.base.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by huazhou.whz on 2016/10/21.
 */
public class GainDeviceIP implements ICommand<String,String> {
    private String mDeviceId;

    public GainDeviceIP(String deviceId){
        mDeviceId = deviceId;
    }

    @Override
    public String parse(String s) {
        if (Utils.isBlank(s)){
            return null;
        }

        try{
            final String[] lines = Utils.removeDaemon(s.split(Config.ENTER));

            if (lines.length > 0){
                final String line = lines[0];
                return getIp(line);
            }

        }catch (Exception e){
            e.printStackTrace();

            Notify.error(e.getMessage());
        }

        return null;
    }

    private String getIp(String line){
        if (Utils.isBlank(line)){
            return null;
        }

        final String startIndex = "inet";
        final String endIndex = "/";
        if (!line.contains(startIndex) || !line.contains(endIndex)){
            return null;
        }

        final String ip = line.substring(line.indexOf(startIndex) + startIndex.length() + 1,line.indexOf(endIndex));

        return ip;
    }

    @Override
    public String getCommand(@NotNull String adbPath) {
        return Utils.concat(adbPath, Config.SPACE,TARGET,Config.SPACE,mDeviceId,Config.SPACE,GAIN_DEVICE_IP);
    }
}
