package adb.wifi.woaiwhz.component;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public abstract class Adapter {
    private JPanel mContainer;

    public void attach(@NotNull JPanel container){
        mContainer = container;
    }

    public void notifyDataChange(){
        if(mContainer != null){
            mContainer.updateUI();
        }
    }

    public abstract int getCount();

    public abstract int getPosition();

    public abstract Component getView();
}
