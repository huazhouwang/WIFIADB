package adb.wifi.woaiwhz.component;

import adb.wifi.woaiwhz.base.Utils;
import adb.wifi.woaiwhz.component.base.BaseAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by wanghuazhou on 01/01/2017.
 */
public class IpHolder extends BaseAdapter.ViewHolder implements MouseListener {
    private JPanel mContentPane;
    private JLabel mIpLabel;
    private JRadioButton mRadio;
    private String mIP;

    IpHolder() {
        mContentPane.addMouseListener(this);
        mContentPane.setCursor(Utils.getHandCursor());
    }

    @Override

    protected Component getRoot() {
        return mContentPane;
    }

    void onBind(@NotNull String ip) {
        mIP = ip;
        mIpLabel.setText(ip);
    }

    public boolean isSelected() {
        return mRadio.isSelected();
    }

    public String getIp() {
        return mIP;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mRadio.setSelected(!mRadio.isSelected());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
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
