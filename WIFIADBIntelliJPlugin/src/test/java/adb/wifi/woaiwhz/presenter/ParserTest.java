package adb.wifi.woaiwhz.presenter;

import adb.wifi.woaiwhz.base.device.Device;
import adb.wifi.woaiwhz.command.AllDevices;
import adb.wifi.woaiwhz.command.GainDeviceIP;
import adb.wifi.woaiwhz.command.ICommand;
import org.junit.Test;

/**
 * Created by huazhou.whz on 2016/10/15.
 */

public class ParserTest {
    @Test
    public void allDevicesTest(){
        String result = "* daemon not running. starting it now on port 5037 *\n" +
                "* daemon started successfully *\n" +
                "List of devices attached\n" +
                "192.168.115.101:5555   device product:vbox86p model:Custom_Phone___5_1_0___API_22___768x1280 device:vbox86p\n" +
                "HC43FW9E0261           device product:aicp_m7 model:One device:m7\n" +
                "\n";

        ICommand<String,Device[]> parser = new AllDevices();
        Device[] devices = parser.parse(result);

        for (Device device : devices) {
            System.out.println(device);
        }
    }

    @Test
    public void getIpTest(){
        final String result = "inet 30.34.168.10/22 brd 30.34.171.255 scope global wlan0";

        ICommand<String,String> command = new GainDeviceIP("");
        final String ip = command.parse(result);

        System.out.println(ip);
    }
}
