package adb.wifi.woaiwhz.component.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by huazhou.whz on 2016/10/15.
 */
public abstract class BaseAdapter {
    private JPanel mContainer;
    private final Set<Component> mComponentPool;

    {
        mComponentPool = new HashSet<Component>();
    }

    public void attach(@NotNull JPanel container){
        if (mContainer != null){
            mContainer.removeAll();
            mContainer.updateUI();
            mComponentPool.clear();
        }

        mContainer = container;
    }

    public void notifyDataChange(){
        if(mContainer == null){
            return;
        }

        final Component[] convertItems = mContainer.getComponents();
        mComponentPool.addAll(Arrays.asList(convertItems));
        mContainer.removeAll();

        final int count = getCount();

        if(count <= 0){
            mContainer.updateUI();
            return;
        }

        final Iterator<Component> iterator = mComponentPool.iterator();
        final List<Component> newItems = new ArrayList<Component>();

        for(int i = 0;i < count;++i){
            final Component component;

            if(iterator.hasNext()) {
                final Component convertItem = iterator.next();
                component = getView(i, convertItem);

                if (component == null) {
                    throw new NullPointerException();
                }
            }else {
                component = getView(i,null);

                if (component == null) {
                    throw new NullPointerException();
                }else {
                    newItems.add(component);
                }
            }

            mContainer.add(component,i);
        }

        mComponentPool.addAll(newItems);
        mContainer.updateUI();
    }

    protected abstract int getCount();

    protected abstract Component getView(int position, @Nullable Component convertItem);
}
