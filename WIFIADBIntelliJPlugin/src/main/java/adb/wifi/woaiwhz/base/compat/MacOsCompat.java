package adb.wifi.woaiwhz.base.compat;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wanghuazhou on 01/01/2017.
 */
class MacOsCompat extends AbOsCompat {

    MacOsCompat() {
        super("mac_os");
    }

    @NotNull
    @Override
    public String getADBinSdk() {
        return "/platform-tools/adb";
    }

    @NotNull
    @Override
    public String getADBName() {
        return "adb";
    }

}
