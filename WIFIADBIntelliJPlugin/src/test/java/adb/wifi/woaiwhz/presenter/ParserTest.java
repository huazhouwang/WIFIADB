package adb.wifi.woaiwhz.presenter;

import adb.wifi.woaiwhz.base.device.Device;
import adb.wifi.woaiwhz.parser.AllDevicesCommand;
import adb.wifi.woaiwhz.parser.ICommand;
import org.junit.Test;

/**
 * Created by huazhou.whz on 2016/10/15.
 */

public class ParserTest {
    @Test
    public void test(){
        String result = "* daemon not running. starting it now on port 5037 *\n" +
                "* daemon started successfully *\n" +
                "List of devices attached\n" +
                "192.168.115.101:5555   device product:vbox86p model:Custom_Phone___5_1_0___API_22___768x1280 device:vbox86p\n" +
                "HC43FW9E0261           device product:aicp_m7 model:One device:m7\n" +
                "\n";

        ICommand<String,Device[]> parser = new AllDevicesCommand();
        Device[] devices = parser.parse(result);

        for (Device device : devices) {
            System.out.println(device);
        }
    }
}
