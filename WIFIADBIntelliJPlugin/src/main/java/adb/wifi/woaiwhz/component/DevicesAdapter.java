package adb.wifi.woaiwhz.component;

import adb.wifi.woaiwhz.base.Device;
import adb.wifi.woaiwhz.component.base.BaseAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by huazhou.whz on 2016/10/16.
 */
public class DevicesAdapter extends BaseAdapter {
    private final List<Device> mDevices;
    private final Map<Component,ViewHolder> mHoldermap;

    {
        mDevices = new ArrayList<Device>();
        mHoldermap = new HashMap<Component, ViewHolder>();
    }

    public void addAll(Collection<Device> devices){
        mDevices.clear();
        mDevices.addAll(devices);
    }

    @Override
    protected int getCount() {
        return mDevices != null ? mDevices.size() : 0;
    }

    @Override
    protected Component getView(int position, @Nullable Component convertItem) {
        final ViewHolder viewHolder;

        if(convertItem == null){
            JLabel label = new JLabel();
            viewHolder = new ViewHolder(label);
            convertItem = label;
            mHoldermap.put(convertItem,viewHolder);
        }else {
            viewHolder = mHoldermap.get(convertItem);
        }

        final Device device = mDevices.get(position);
        viewHolder.onBind(device);

        return convertItem;
    }

    private static class ViewHolder{
        private final JLabel mDeviceIdLabel;

        private ViewHolder(@NotNull JLabel deviceIdLabel){
            mDeviceIdLabel = deviceIdLabel;
        }

        private void onBind(@NotNull Device device){
            mDeviceIdLabel.setText(device.id);
        }
    }
}
