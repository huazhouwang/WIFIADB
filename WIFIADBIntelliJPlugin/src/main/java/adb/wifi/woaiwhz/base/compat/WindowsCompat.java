package adb.wifi.woaiwhz.base.compat;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wanghuazhou on 01/01/2017.
 */
class WindowsCompat extends AbOsCompat {

    WindowsCompat() {
        super("windows");
    }

    @NotNull
    @Override
    public String getADBinSdk() {
        return "/platform-tools/adb.exe";
    }
}
