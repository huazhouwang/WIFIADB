package adb.wifi.woaiwhz.component;

import adb.wifi.woaiwhz.base.Utils;
import adb.wifi.woaiwhz.base.device.Device;
import adb.wifi.woaiwhz.base.device.LocalDevice;
import adb.wifi.woaiwhz.base.device.RemoteDevice;
import adb.wifi.woaiwhz.component.base.BaseAdapter;
import adb.wifi.woaiwhz.listener.OnClickAdapter;
import adb.wifi.woaiwhz.presenter.RootPresenter;
import org.bouncycastle.util.Strings;
import org.jdesktop.swingx.VerticalLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huazhou.whz on 2016/10/16.
 */
public class DevicesAdapter extends BaseAdapter<DevicesAdapter.BaseViewHolder>{
    private final int REMOTE_DEVICE_TITLE = 1;
    private final int REMOTE_DEVICE_ITEM = 1 << 1;
    private final int LOCAL_DEVICE_TITLE = 1 << 3;
    private final int LOCAL_DEVICE_ITEM = 1 << 4;

    private final Icon[] mIcons;

    private final List<Item> mItems;
    private RootPresenter mPresenter;

    public DevicesAdapter(@NotNull RootPresenter presenter){
        mPresenter = presenter;

        mItems = new ArrayList<Item>();

        final Class clazz = getClass();
        mIcons = new Icon[]{
                new ImageIcon(clazz.getResource("/icons/device-remote-icon.png")),
                new ImageIcon(clazz.getResource("/icons/disconnect-icon.png")),
                new ImageIcon(clazz.getResource("/icons/device-local-icon.png")),
                new ImageIcon(clazz.getResource("/icons/connect-icon.png"))
        };
    }

    public void addAll(Device[] devices){
        mItems.clear();

        if (devices == null || devices.length == 0){
            return;
        }

        int localDevicesCount = 0;
        int remoteDevicesCount = 0;

        for (final Device device : devices){
            if(device instanceof LocalDevice){
                ++localDevicesCount;
            }else if(device instanceof RemoteDevice){
                ++remoteDevicesCount;
            }
        }

        Arrays.sort(devices,DeviceComparator.INSTANCE);

        if (remoteDevicesCount > 0){
            mItems.add(new Item(REMOTE_DEVICE_TITLE,null));

            for (int i = 0;i < remoteDevicesCount;++i){
                mItems.add(new Item(REMOTE_DEVICE_ITEM,devices[i]));
            }
        }

        if (localDevicesCount > 0){
            mItems.add(new Item(LOCAL_DEVICE_TITLE,null));

            final int length = devices.length;
            for (int i = remoteDevicesCount;i < length;++i){
                mItems.add(new Item(LOCAL_DEVICE_ITEM,devices[i]));
            }
        }
    }

    @Override
    protected int getItemCount() {
        return mItems.size();
    }

    @Override
    protected int getItemViewType(int position) {
        return mItems.get(position).type;
    }

    @Override
    protected BaseViewHolder onCreateViewHolder(int viewType) {
        switch (viewType){
            case REMOTE_DEVICE_TITLE:
                return new RemoteTitleItemHolder();

            case REMOTE_DEVICE_ITEM:
                return new RemoteDeviceHolder(mIcons[0],mIcons[1]);

            case LOCAL_DEVICE_TITLE:
                return new LocalTitleItemHolder();

            case LOCAL_DEVICE_ITEM:
                return new LocalDeviceHolder(mIcons[2],mIcons[3]);

            default:
                return null;
        }
    }

    @Override
    protected void onBindViewHolder(BaseViewHolder holder, int position) {
        final Item item = mItems.get(position);
        holder.onBind(item.obj);

        if (holder instanceof RemoteDeviceHolder){
            final RemoteDeviceHolder realHolder = (RemoteDeviceHolder) holder;

            realHolder.setOnClick(new OnClickAdapter() {
                @Override
                public void onClick(AWTEvent e) {
                    final Device device = realHolder.getDevice();

                    if (device != null){
                        mPresenter.disconnectRemoteDevice(device.id);
                    }
                }
            });
        }else if (holder instanceof LocalDeviceHolder){
            final LocalDeviceHolder realHolder = (LocalDeviceHolder) holder;

            realHolder.setOnClick(new OnClickAdapter() {
                @Override
                public void onClick(AWTEvent e) {
                    final Device device = realHolder.getDevice();

                    if (device != null){
                        mPresenter.connectLocalDevice(device.id);
                    }
                }
            });
        }
    }

    static abstract class BaseViewHolder extends BaseAdapter.ViewHolder{
        protected abstract void onBind(@Nullable Object device);
    }

    public abstract static class DeviceHolder extends BaseViewHolder{
        protected JPanel mItemRoot;
        protected JLabel mDeviceLabel;
        protected JLabel mIdLabel;
        protected JPanel mContentLayout;
        protected JLabel mTypeIconLabel;
        protected JLabel mActionIconLabel;
        protected final JPanel mLabelLayout;

        public Device mDevice;

        public DeviceHolder(){
            mLabelLayout = new JPanel(new VerticalLayout());
            mDeviceLabel = new JLabel();
            mIdLabel = new JLabel();

            mItemRoot.setPreferredSize(new Dimension(-1,36));
            mLabelLayout.add(mDeviceLabel);
            mLabelLayout.add(mIdLabel);
            mContentLayout.add(mLabelLayout);

            mActionIconLabel.setCursor(Utils.getHandCursor());
        }

        @Override
        protected void onBind(@Nullable Object obj) {
            if (!(obj instanceof Device)){
                return;
            }

            mDevice = (Device) obj;
            bindDevice(mDevice);
        }

        protected void bindDevice(Device device){
            mDeviceLabel.setText(Strings.toUpperCase(device.device));
            mIdLabel.setText(Strings.toUpperCase(device.id));
        }

        public Device getDevice(){
            return mDevice;
        }

        @Override
        protected Component getItem() {
            return mItemRoot;
        }

        public void setOnClick(OnClickAdapter onClick){
            cleanMouseListener();
            mActionIconLabel.addMouseListener(onClick);
        }

        private void cleanMouseListener(){
            final MouseListener[] listeners = mActionIconLabel.getMouseListeners();

            if (listeners != null){
                for (MouseListener listener : listeners){
                    mActionIconLabel.removeMouseListener(listener);
                }
            }
        }
    }

    public static class RemoteDeviceHolder extends DeviceHolder{
        private RemoteDeviceHolder(@NotNull Icon typeIcon, @NotNull Icon actionIcon){
            mTypeIconLabel.setIcon(typeIcon);
            mActionIconLabel.setIcon(actionIcon);
        }
    }

    public static class LocalDeviceHolder extends DeviceHolder{
        private LocalDeviceHolder(@NotNull Icon typeIcon,@NotNull Icon actionIcon){
            mTypeIconLabel.setIcon(typeIcon);
            mActionIconLabel.setIcon(actionIcon);
        }
    }

    private static class DeviceComparator implements Comparator<Device> {

        private static final Comparator<Device> INSTANCE = new DeviceComparator();

        @Override
        public int compare(Device o1, Device o2) {
            if (o1 instanceof RemoteDevice && !(o2 instanceof RemoteDevice)){
                return -1;
            }else if(!(o1 instanceof RemoteDevice) && o2 instanceof RemoteDevice){
                return 1;
            }

            return 0;
        }
    }

    static abstract class BaseTitleItemHolder extends BaseViewHolder{
        protected JPanel mItemRoot;
        protected JLabel mTitleLabel;

        BaseTitleItemHolder(){
            mItemRoot.setPreferredSize(new Dimension(-1,24));
        }

        @Override
        protected Component getItem() {
            return mItemRoot;
        }
    }

    private static class RemoteTitleItemHolder extends BaseTitleItemHolder {

        @Override
        protected void onBind(@Nullable Object obj) {
            mTitleLabel.setText(Strings.toUpperCase("Remote Devices"));
        }
    }

    private static class LocalTitleItemHolder extends BaseTitleItemHolder {
        @Override
        protected void onBind(@Nullable Object obj) {
            mTitleLabel.setText(Strings.toUpperCase("Local Devices"));
        }
    }

    private static class Item{
        final int type;
        final Object obj;

        private Item(int type, Object obj) {
            this.type = type;
            this.obj = obj;
        }
    }
}
