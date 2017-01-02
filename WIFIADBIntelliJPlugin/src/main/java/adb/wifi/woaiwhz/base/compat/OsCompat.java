package adb.wifi.woaiwhz.base.compat;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wanghuazhou on 01/01/2017.
 */
public class OsCompat implements IOsCompat{
    private static final IOsCompat mProxy;
    private static final IOsCompat mInstance;

    static {
        mProxy = checkOs();
        mInstance = new OsCompat();
    }

    @NotNull
    public static IOsCompat instance() {
        return mInstance;
    }

    @NotNull
    @Override
    public String getADBinSdk() {
        return mProxy.getADBinSdk();
    }

    @NotNull
    @Override
    public String getADBName() {
        return mProxy.getADBName();
    }

    private static IOsCompat checkOs() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("windows")) {
            return new WindowsCompat();
        } else if (os.contains("mac")){
            return new MacOsCompat();
        } else {
            return new LinuxCompat();
        }
    }
}
