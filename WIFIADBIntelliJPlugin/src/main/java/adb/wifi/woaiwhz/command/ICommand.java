package adb.wifi.woaiwhz.command;

import org.jetbrains.annotations.NotNull;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public interface ICommand<Input,Output>{
    String TARGET = "-s";
    String FIND_ALL_DEVICES = "devices -l";
    String CONNECT_DEVICE = "connect";
    String DISCONNECT_DEVICE = "disconnect";
    String GAIN_DEVICE_IP = "shell ip -f inet addr show wlan0 | grep \"inet\"";
    String ALERT_PORT = "tcpip";

    Output parse(Input input);
    String getCommand(@NotNull String adbPath);
}
