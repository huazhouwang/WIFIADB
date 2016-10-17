package adb.wifi.woaiwhz.base.device;

import adb.wifi.woaiwhz.base.Utils;
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

    public static class Builder{
        private final boolean mIsRemote;

        private String id;
        private boolean state;
        private String product;
        private String model;
        private String device;

        public Builder(boolean isRemote){
            mIsRemote = isRemote;
        }

        public Builder id(String id){
            this.id = id;

            return this;
        }

        public Builder state(boolean state){
            this.state = state;

            return this;
        }

        public Builder product(String product){
            this.product = product;

            return this;
        }

        public Builder model(String model){
            this.model = model;

            return this;
        }

        public Builder device(String device){
            this.device = device;

            return this;
        }

        public Device build(){
            if(mIsRemote){
                return new RemoteDevice(id,state,product,model,device);
            }else {
                return new LocalDevice(id,state,product,model,device);
            }
        }
    }
}
