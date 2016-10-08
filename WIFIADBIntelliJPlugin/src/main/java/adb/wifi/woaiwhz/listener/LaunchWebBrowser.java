package adb.wifi.woaiwhz.listener;

import adb.wifi.woaiwhz.component.Notify;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by huazhou.whz on 2016/10/7.
 */
public class LaunchWebBrowser implements MouseListener {
    private final String mUrl;

    public LaunchWebBrowser(@NotNull String url){
        mUrl = url;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            Desktop.getDesktop().browse(new java.net.URI(mUrl));
        }catch (Exception exception){
            Notify.error();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
