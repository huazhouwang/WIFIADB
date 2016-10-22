package adb.wifi.woaiwhz.window;

import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Notify;
import adb.wifi.woaiwhz.base.Utils;
import adb.wifi.woaiwhz.base.device.Device;
import adb.wifi.woaiwhz.component.DevicesAdapter;
import adb.wifi.woaiwhz.component.ListView;
import adb.wifi.woaiwhz.listener.*;
import adb.wifi.woaiwhz.listener.MouseAdapter;
import adb.wifi.woaiwhz.presenter.RootPresenter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by huazhou.whz on 2016/10/7.
 */
public class RootWindow implements ToolWindowFactory,ActionListener,RootPresenter.RootView{
    private JPanel mRoot;
    private JTextField mIP_1;
    private JTextField mIP_2;
    private JTextField mIP_3;
    private JTextField mIP_4;
    private JTextField mPort;
    private JTextField[] mIPTexts;
    private JButton mConnectButton;
    private JPanel mEmptyLayout;
    private JLabel mHelpLabel;
    private JPanel mContentLayout;
    private JPanel mLoadingLayout;
    private JPanel mCenterLayout;
    private JPanel mFunctionLayout;
    private JLabel mProgressTip;
    private JScrollPane mScrollPane;
    private JPanel mActivePane;
    private JLabel mRefreshLabel;

    private final DevicesAdapter mAdapter;
    private final RootPresenter mPresenter;

    {
        mPresenter = new RootPresenter(this);

        final ListView devicesList = new ListView();
        mActivePane.add(devicesList);
        mAdapter = new DevicesAdapter(mPresenter);
        devicesList.setAdapter(mAdapter);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        init();
        attach2ToolWindow(toolWindow);
        attach2Project(project);
    }

    private void attach2ToolWindow(ToolWindow toolWindow){
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mRoot, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void attach2Project(Project project){
        mPresenter.init(project);
        mPresenter.getAllDevices();
    }

    private void init(){
        initIpText();
        initPortText();
        initConnectButton();
        initOthersComponent();
        listenOthersKeyInput();
    }

    private void initConnectButton(){
        mConnectButton.setCursor(Utils.getHandCursor());
        mConnectButton.addActionListener(this);
    }

    private void initPortText(){
        final NumberDocumentFilter documentFilter = new NumberDocumentFilter(5);
        final InputVerifier verifier = new CustomInputVerifier(1 << 16,Integer.valueOf(Config.DEFAULT_PORT));
        final FocusListener focusListener = new SelectAllListener();

        documentFilter.bind(mPort);
        mPort.setInputVerifier(verifier);
        mPort.addFocusListener(focusListener);

        mPort.setText(Config.DEFAULT_PORT);
    }

    private void initIpText(){
        mIPTexts = new JTextField[]{
                mIP_1,mIP_2,mIP_3,mIP_4
        };

        final InputVerifier verifier = new CustomInputVerifier(1 << 8);
        final FocusListener focusListener = new SelectAllListener();

        for (JTextField item : mIPTexts){
            final NumberDocumentFilter documentFilter = new NumberDocumentFilter(3);

            documentFilter.bind(item);
            item.setInputVerifier(verifier);
            item.addFocusListener(focusListener);
        }
    }

    private void listenOthersKeyInput(){
        final JTextField[] textFields = new JTextField[]{
                mIP_1,mIP_2,mIP_3,mIP_4,mPort
        };

        final KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                final int key = e.getKeyChar();

                if(key == KeyEvent.VK_ENTER){
                    handleConnection();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        };

        final KeyListener autoFocusNext = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                final int key = e.getKeyChar();

                if(key == KeyEvent.VK_PERIOD){
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        };

        for (JTextField field : textFields){
            field.addKeyListener(enterKeyListener);

            if(field == mIP_4 || field == mPort){
                continue;
            }

            field.addKeyListener(autoFocusNext);
        }
    }

    private void initOthersComponent() {
        mHelpLabel.addMouseListener(new LaunchWebBrowser(Config.HELP));
        mHelpLabel.setCursor(Utils.getHandCursor());

        mRefreshLabel.setCursor(Utils.getHandCursor());
        mRefreshLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mPresenter.getAllDevices();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();

        if(source == mConnectButton){
            handleConnection();
        }
    }

    private void handleConnection(){
        if(!verifyIpText() || !verifyPortText()){
            Notify.error("Fail to verify ip address or port number");
            return;
        }

        final String deviceId = gainIpAddressWithPortNumber();
        mPresenter.addDevice(deviceId);
    }

    private String gainIpAddressWithPortNumber(){
        final StringBuilder builder = new StringBuilder();

        for (JTextField field : mIPTexts){
            builder.append(field.getText());
            builder.append(".");
        }

        final int length = builder.length();
        builder.replace(length - 1,length,":");
        builder.append(mPort.getText());

        return builder.toString();
    }

    private boolean verifyPortText(){
        final String text = mPort.getText();

        if(TextUtils.isBlank(text)){
            return false;
        }

        try {
            int port = Integer.valueOf(text);

            if(port >= 0 && port < 1 << 16){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();

        }

        return false;
    }

    @SuppressWarnings("unused")
    private void cleanText(){
        for(JTextField field : mIPTexts){
            field.setText("");
        }
        mPort.setText(Config.DEFAULT_PORT);
    }

    private boolean verifyIpText(){
        for (JTextField field : mIPTexts){
            final String text = field.getText();

            if(TextUtils.isBlank(text)){
                return false;
            }

            try{
                int ip = Integer.valueOf(text);
                if(ip < 0 || ip >= 1 << 8){
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    @Override
    public void onADBEmpty() {
        Notify.error("Cannot find adb,please check this project's android sdk and restart IDE");

        for (Component component : mIPTexts){
            component.setEnabled(false);
        }

        mPort.setEnabled(false);
        mConnectButton.setEnabled(false);
        mRefreshLabel.setEnabled(false);
    }

    @Override
    public void onADBComplete(String path) {
        for (Component component : mIPTexts){
            component.setEnabled(true);
        }

        mPort.setEnabled(true);
        mConnectButton.setEnabled(true);
        mRefreshLabel.setEnabled(true);
    }

    @Override
    public void showLoading() {
        mCenterLayout.removeAll();
        mCenterLayout.add(mLoadingLayout);
        mCenterLayout.updateUI();
    }

    @Override
    public void hideLoading() {
        mCenterLayout.removeAll();
        mCenterLayout.add(mFunctionLayout);
        mCenterLayout.updateUI();
    }

    @Override
    public void refreshDevices(@Nullable Device[] devices) {
        if (devices == null || devices.length == 0){
            mContentLayout.removeAll();
            mContentLayout.add(mEmptyLayout);
            return;
        }

        mContentLayout.removeAll();
        mContentLayout.add(mScrollPane);

        mAdapter.addAll(devices);
        mAdapter.notifyDataSetChange();
    }

    @Override
    public void refreshProgressTip(@NotNull String tip) {
        mProgressTip.setText(tip);
    }
}
