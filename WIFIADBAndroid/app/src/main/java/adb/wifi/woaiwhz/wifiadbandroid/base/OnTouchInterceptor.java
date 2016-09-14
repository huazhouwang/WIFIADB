package adb.wifi.woaiwhz.wifiadbandroid.base;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by huazhou.whz on 2016/9/14.
 */
public class OnTouchInterceptor implements View.OnTouchListener {
    /**
     * do nothing,just intercept touch even
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
