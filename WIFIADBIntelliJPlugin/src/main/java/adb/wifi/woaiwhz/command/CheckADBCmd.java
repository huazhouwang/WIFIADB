package adb.wifi.woaiwhz.command;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by wanghuazhou on 31/12/2016.
 */
public class CheckADBCmd implements ICommand<String, Boolean> {
    @Override
    public Boolean parse(String string) {
        return string.contains("Android Debug Bridge version");
    }

    @Override
    public String getCommand(@NotNull String adbPath) {
        return Utils.concat(adbPath, Config.SPACE, "version");
    }
}
