package adb.wifi.woaiwhz.command;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by wanghuazhou on 30/12/2016.
 */
public class KillServer implements ICommand<Void,Void>{

    @Override
    public Void parse(Void aVoid) {
        return null;
    }

    @Override
    public String getCommand(@NotNull String adbPath) {
        return Utils.concat(adbPath, Config.SPACE, "kill-server");
    }
}
