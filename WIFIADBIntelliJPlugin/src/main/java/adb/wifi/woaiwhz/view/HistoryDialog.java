package adb.wifi.woaiwhz.view;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Global;
import adb.wifi.woaiwhz.base.Properties;
import adb.wifi.woaiwhz.component.IpAdapter;
import adb.wifi.woaiwhz.component.ListPanel;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;

/**
 * Created by wanghuazhou on 01/01/2017.
 */
public class HistoryDialog extends DialogWrapper {

    private JPanel mRoot;
    private JPanel mContent;
    private final IpAdapter mAdapter;
    private final Callback mCallback;

    public HistoryDialog(@NotNull Callback callback) {
        super(Global.instance().project());
        setTitle("History");
        init();

        mCallback = callback;

        String[] array = Properties.instance().appLevel().getValues(Config.IP_HISTORY);
        if (array == null || array.length == 0) {
            mAdapter = null;
            setOKActionEnabled(false);
        } else {
            mAdapter = new IpAdapter(Arrays.asList(array));
            initList();
        }
    }

    private String[] test() {
        return new String[] {
                "192.160.833.2:5555",
                "192.160.833.2:5555",
                "192.160.833.2:5555",
                "192.160.833.2:5555",
                "192.160.803.2:5555",
                "199.31.44.55:5555",
                "199.31.44.55:5555",
                "199.31.44.55:5555",
                "10.3.5.18:5555",
                "10.3.5.18:5555"
        };
    }

    private void initList() {
        final ListPanel panel = new ListPanel();
        panel.setAdapter(mAdapter);

        mContent.add(panel);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mRoot;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();

        final String[] result = mAdapter.getSelectedIps();

        if (result != null && result.length != 0) {
            mCallback.gainIpFromHistory(result);
        }
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();
    }

    public interface Callback {
        void gainIpFromHistory(@NotNull String[] ip);
    }
}
