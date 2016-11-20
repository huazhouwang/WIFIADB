package adb.wifi.woaiwhz.listener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Created by huazhou.whz on 2016/10/21.
 */
public abstract class OnClickAdapter extends MouseAdapter implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        onClick(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        onClick(e);
    }

    public abstract void onClick(AWTEvent e);
}
