package adb.wifi.woaiwhz.wifiadbandroid.bean;

import android.util.Property;
import android.view.View;

/**
 * Created by huazhou.whz on 2016/10/6.
 */

public class YProperty extends Property<View, Float> {

    public YProperty(Class<Float> type, String name) {
        super(type, name);
    }

    @Override
    public Float get(View object) {
        return object.getY();
    }

    @Override
    public void set(View object, Float value) {
        object.setY(value);
    }
}
