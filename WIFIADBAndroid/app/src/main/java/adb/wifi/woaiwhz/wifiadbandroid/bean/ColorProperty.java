package adb.wifi.woaiwhz.wifiadbandroid.bean;

import android.graphics.drawable.GradientDrawable;
import android.util.Property;

/**
 * Created by huazhou.whz on 2016/10/6.
 */

public class ColorProperty extends Property<GradientDrawable,Integer> {

    public ColorProperty(Class<Integer> type, String name) {
        super(type, name);
    }

    @Override
    public Integer get(GradientDrawable object) {
        return 0;
    }

    @Override
    public void set(GradientDrawable object, Integer value) {
        object.setColor(value);
    }
}
