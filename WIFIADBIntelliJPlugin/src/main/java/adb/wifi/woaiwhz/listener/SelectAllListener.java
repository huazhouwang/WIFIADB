package adb.wifi.woaiwhz.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by huazhou.whz on 2016/10/21.
 */
public class SelectAllListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent e) {
        final Component current = e.getComponent();

        if (current instanceof JTextField){
            ((JTextField) current).selectAll();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
