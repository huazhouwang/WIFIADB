package adb.wifi.woaiwhz.component;

import adb.wifi.woaiwhz.component.base.BaseAdapter;
import com.intellij.ui.components.panels.VerticalLayout;

import javax.swing.*;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public class ListView extends JPanel {
    private BaseAdapter mAdapter;

    public ListView() {
        super(new VerticalLayout(10));
    }

    public void setAdapter(BaseAdapter adapter){
        mAdapter = adapter;
        mAdapter.attach(this);
        mAdapter.notifyDataSetChange();
    }
}
