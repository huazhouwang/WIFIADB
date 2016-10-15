package adb.wifi.woaiwhz.parser;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public interface ICommand<Input,Output>{
    String FIND_ALL_DEVICES = "devices -l";
    String CONNECT_DEVICE = "connect";

    Output parse(Input input);
    String getCommand();
}
