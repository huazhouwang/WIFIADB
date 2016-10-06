package adb.wifi.woaiwhz.wifiadbandroid.bean;

import android.util.Property;
import android.view.View;

/**
 * Created by huazhou.whz on 2016/10/6.
 */

public class ScaleProperty extends Property<View, Float> {

    public ScaleProperty(Class<Float> type, String name) {
        super(type, name);
    }

    @Override
    public Float get(View object) {
        return object.getScaleX();
    }

    @Override
    public void set(View object, Float value) {
        object.setScaleX(value);
        object.setScaleY(value);
    }
}
