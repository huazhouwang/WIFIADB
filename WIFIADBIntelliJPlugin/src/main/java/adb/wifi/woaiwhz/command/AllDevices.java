package adb.wifi.woaiwhz.command;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Notify;
import adb.wifi.woaiwhz.base.Utils;
import adb.wifi.woaiwhz.base.device.Device;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public class AllDevices implements ICommand<String,Device[]> {
    private static String USELESS = "List of devices attached";
    private static String CONNECTED_STATE = "device";
    private static String INDEX_OF_PRODUCT = "product:";
    private static String INDEX_OF_MODEL = "model:";
    private static String INDEX_OF_DEVICE = "device:";

    @Override
    public Device[] parse(String s) {
        if (Utils.isBlank(s)){
            return null;
        }

        try {
            final String[] originalLines = Utils.removeDaemon(s.split(Config.ENTER));
            final String[] lines = removeUseless(originalLines);

            final List<Device> list = new ArrayList<Device>();

            for (final String line : lines){
                final String[] items = line.split(Config.ANY_SPACES);

                final boolean state = getItem(items,1).equals(CONNECTED_STATE);

                if(!state){
                    continue;
                }

                final String id = getItem(items,0);

                final Device.Builder builder = verifyDeviceType(id);

                if(builder == null){
                    continue;
                }

                final Device device = builder.state(true)
                        .product(getValue(getItem(items,2),INDEX_OF_PRODUCT))
                        .model(getValue(getItem(items,3),INDEX_OF_MODEL))
                        .device(getValue(getItem(items,4),INDEX_OF_DEVICE))
                        .build();

                list.add(device);
            }

            final Device[] devices = new Device[list.size()];

            return list.toArray(devices);
        }catch (Exception e){
            final String track = e.getMessage();
            Notify.error(track);
            return null;
        }
    }

    private Device.Builder verifyDeviceType(String deviceId){
        if (Utils.isBlank(deviceId)){
            return null;
        }

        return new Device.Builder(Utils.isRemoteDevice(deviceId))
                .id(deviceId);
    }

    private String[] removeUseless(@NotNull String[] originalLines){
        final List<String> list = new ArrayList<String>();

        for (String line : originalLines){
            if(line.contains(USELESS)){
                final int start = line.indexOf(USELESS) + USELESS.length();
                final int end = line.length();

                if(start < end) {
                    line = line.substring(start, end);
                }else {
                    line = Config.EMPTY;
                }
            }

            if(!Utils.isBlank(line)){
                list.add(line);
            }
        }

        final String[] lines = new String[list.size()];

        return list.toArray(lines);
    }

    private String getItem(String[] items,int index){
        if(items.length > index){
            return items[index];
        }else {
            return Config.EMPTY;
        }
    }

    @Override
    public String getCommand(@NotNull String adbPath) {
        return Utils.concat(adbPath, Config.SPACE, FIND_ALL_DEVICES);
    }

    private String getValue(@NotNull String line,@NotNull String key){
        if(!line.contains(key)){
            return Config.EMPTY;
        }

        final String value = line.substring(key.length(),line.length());

        if (Utils.isBlank(value)){
            return Config.EMPTY;
        }else {
            return value;
        }
    }
}
