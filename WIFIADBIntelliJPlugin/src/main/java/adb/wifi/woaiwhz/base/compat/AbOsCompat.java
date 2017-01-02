package adb.wifi.woaiwhz.base.compat;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wanghuazhou on 01/01/2017.
 */
abstract class AbOsCompat implements IOsCompat {

    private String mDec;

    AbOsCompat(@NotNull String dec) {
        mDec = dec;
    }

    public String which() {
        return mDec;
    }
}
