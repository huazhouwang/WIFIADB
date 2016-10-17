package adb.wifi.woaiwhz.component;

import adb.wifi.woaiwhz.base.device.Device;
import adb.wifi.woaiwhz.base.device.LocalDevice;
import adb.wifi.woaiwhz.base.device.RemoteDevice;
import adb.wifi.woaiwhz.component.base.BaseAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by huazhou.whz on 2016/10/16.
 */
public class DevicesAdapter extends BaseAdapter<DevicesAdapter.BaseViewHolder> {
    private final List<Device> mDevices;

    {
        mDevices = new ArrayList<Device>();
    }

    public void addAll(Collection<Device> devices){
        mDevices.clear();
        mDevices.addAll(devices);
    }

    @Override
    protected int getItemCount() {
        return mDevices != null ? mDevices.size() : 0;
    }

    @Override
    protected int getItemViewType(int position) {
        final Device device = mDevices.get(position);

        if(device instanceof RemoteDevice){
            return RemoteDeviceHolder.class.hashCode();
        }else if(device instanceof LocalDevice){
            return LocalDeviceHolder.class.hashCode();
        }else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected BaseViewHolder onCreateViewHolder(int viewType) {
        if(viewType == LocalDeviceHolder.class.hashCode()){
            return new LocalDeviceHolder();
        }else if (viewType == RemoteDeviceHolder.class.hashCode()){
            return new RemoteDeviceHolder();
        }else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected void onBindViewHolder(BaseViewHolder holder, int position) {
        final Device device = mDevices.get(position);

        if (device == null){
            throw new NullPointerException();
        }

        if (holder instanceof LocalDeviceHolder){
            ((LocalDeviceHolder) holder).onBind((LocalDevice) device);
        }else if(holder instanceof RemoteDeviceHolder){
            ((RemoteDeviceHolder) holder).onBind((RemoteDevice) device);
        }else {
            throw new IllegalArgumentException();
        }
    }

    static abstract class BaseViewHolder<T extends Device> extends BaseAdapter.ViewHolder{
        protected abstract void onBind(@NotNull T device);
    }

    private static class RemoteDeviceHolder extends BaseViewHolder<RemoteDevice>{
        private final JLabel mDeviceIdLabel;

        private RemoteDeviceHolder(){
            mDeviceIdLabel = new JLabel();
        }

        @Override
        protected void onBind(@NotNull RemoteDevice device) {
            mDeviceIdLabel.setText("Remote = " + device.id);
        }

        @Override
        protected Component getItem() {
            return mDeviceIdLabel;
        }
    }

    private static class LocalDeviceHolder extends BaseViewHolder<LocalDevice>{
        private final JLabel mDeviceIdLabel;

        private LocalDeviceHolder(){
            mDeviceIdLabel = new JLabel();
        }

        @Override
        protected void onBind(@NotNull LocalDevice device) {
            mDeviceIdLabel.setText("Local = " + device.id);
        }

        @Override
        protected Component getItem() {
            return mDeviceIdLabel;
        }
    }
}
