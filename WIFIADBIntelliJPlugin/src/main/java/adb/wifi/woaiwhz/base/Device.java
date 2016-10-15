package adb.wifi.woaiwhz.base;

import org.jetbrains.annotations.Nullable;

/**
 * Created by huazhou.whz on 2016/10/14.
 */
public class Device {
    public final static String UNKNOWN = "UNKNOWN";
    public final String id;
    public final boolean state;
    public final String product;
    public final String model;
    public final String device;

    public Device(String id,boolean state,String product,String model,String device) {
        this.id = convert(id);
        this.state = state;
        this.product = product;
        this.model = convert(model);
        this.device = convert(device);
    }

    private String convert(@Nullable String text){
        return Utils.isBlank(text) ? UNKNOWN : text;
    }

    @Override
    public String toString() {
        return Utils.concat("[",id,"],",
                "[",state + "","],",
                "[",product,"],",
                "[",model,"],",
                "[",device,"]");
    }
}
