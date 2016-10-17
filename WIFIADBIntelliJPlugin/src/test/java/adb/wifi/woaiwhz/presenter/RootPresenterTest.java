package adb.wifi.woaiwhz.presenter;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public class RootPresenterTest {

    @Test
    public void test(){
        System.out.println(Pattern.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}","33.34.168.10:5555"));
    }
}