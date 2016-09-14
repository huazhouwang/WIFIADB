package adb.wifi.woaiwhz.wifiadbandroid;

import android.app.Application;
import android.content.Context;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public class MyApp extends Application{

    private static Application mInstance;

    public MyApp() {
        super();

        mInstance = this;
    }

    public static Context getContext(){
        return mInstance.getApplicationContext();
    }
}
