package adb.wifi.woaiwhz.listener;

import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Created by huazhou.whz on 2016/10/21.
 */
public abstract class OnClickAdapter extends MouseAdapter implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        onClick(e,null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        onClick(null,e);
    }

    public abstract void onClick(@Nullable ActionEvent e1,@Nullable MouseEvent e2);
}
