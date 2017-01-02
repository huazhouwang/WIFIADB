package adb.wifi.woaiwhz.component;

import adb.wifi.woaiwhz.component.base.BaseAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghuazhou on 01/01/2017.
 */
public class IpAdapter extends BaseAdapter<IpHolder> {
    private static final int IP_INFO = 1;
    private final List<String> mIpList;

    public IpAdapter(@NotNull List<String> ipList) {
        mIpList = ipList;
    }

    @Override
    protected int getItemCount() {
        return mIpList.size();
    }

    @Override
    protected int getItemViewType(int position) {
        return IP_INFO;
    }

    @Override
    protected IpHolder onCreateViewHolder(int viewType) {
        return new IpHolder();
    }

    @Override
    protected void onBindViewHolder(IpHolder holder, int position) {
        holder.onBind(mIpList.get(position));
    }

    @Nullable
    public String[] getSelectedIps() {
        if (mHolderList.isEmpty()) {
            return null;
        }

        final List<String> list = new ArrayList<>();

        for (IpHolder holder : mHolderList) {
            if (holder.isSelected()) {
                list.add(holder.getIp());
            }
        }

        return list.toArray(new String[list.size()]);
    }
}
