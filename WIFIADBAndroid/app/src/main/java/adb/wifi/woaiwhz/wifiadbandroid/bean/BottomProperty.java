package adb.wifi.woaiwhz.wifiadbandroid.bean;

import android.util.Property;
import android.view.View;

/**
 * Created by huazhou.whz on 2016/10/6.
 */

public class BottomProperty extends Property<View, Integer> {

    public BottomProperty(Class<Integer> type, String name) {
        super(type, name);
    }

    @Override
    public Integer get(View object) {
        return object.getBottom();
    }

    @Override
    public void set(View object, Integer value) {
        object.setBottom(value);
    }
}
