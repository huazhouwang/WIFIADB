package adb.wifi.woaiwhz.listener;

import adb.wifi.woaiwhz.base.Notify;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by huazhou.whz on 2016/10/7.
 */
public class LaunchWebBrowser extends MouseAdapter {
    private final String mUrl;

    public LaunchWebBrowser(@NotNull String url){
        mUrl = url;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            Desktop.getDesktop().browse(new java.net.URI(mUrl));
        }catch (Exception exception){
            Notify.error("Cannot launch web browser");
        }
    }
}
