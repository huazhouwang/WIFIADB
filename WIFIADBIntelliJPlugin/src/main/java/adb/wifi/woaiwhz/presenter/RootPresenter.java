package adb.wifi.woaiwhz.presenter;

import adb.wifi.woaiwhz.base.CommandExecute;
import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Notify;
import adb.wifi.woaiwhz.base.Utils;
import adb.wifi.woaiwhz.base.device.Device;
import adb.wifi.woaiwhz.command.*;
import adb.wifi.woaiwhz.dispatch.Executor;
import adb.wifi.woaiwhz.dispatch.MainThreadHandler;
import adb.wifi.woaiwhz.dispatch.Message;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by huazhou.whz on 2016/10/14.
 */
public class RootPresenter {
    private final RootView mViewLayer;
    private final MainThreadHandler mHandler;

    private Project mProject;
    private String mAdbPath;
    private boolean mRunning;

    public RootPresenter(@NotNull RootView view){
        mViewLayer = view;
        mHandler = new CustomHandler();
        mRunning = false;
    }

    public void init(@NotNull Project project){
        mProject = project;
        mAdbPath = Utils.getAdbPath(mProject);

        if(isAdbEmpty()){
            mViewLayer.onADBFail();
        }else {
            mViewLayer.onADBSuccess(mAdbPath);
        }
    }

    private boolean isAdbEmpty(){
        return Utils.isBlank(mAdbPath);
    }

    public void addDevice(final String deviceId){
        if (!canRun()){
            return;
        }

        if(Utils.isBlank(deviceId)){
            Notify.error();
            return;
        }

        lock();

        runOnPooledThread(new Runnable() {
            @Override
            public void run() {
                addDeviceUnchecked(deviceId);

                getDevicesUnchecked();
            }
        });
    }

    public void rebootServer() {
        killServer();
    }

    private void killServer() {
        if (!canRun()) {
            return;
        }

        lock();

        runOnPooledThread(new Runnable() {
            @Override
            public void run() {
                killServerUnchecked();
            }
        });
    }

    private void killServerUnchecked() {
        mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP, "Kill ADB server...");

        final ICommand command = new KillCommand();
        CommandExecute.execute(command.getCommand(mAdbPath));

        mHandler.sendEmptyMessage(CustomHandler.POST_START_SERVER);
    }

    private void startServer() {
        if (!canRun()) {
            return;
        }

        lock();

        runOnPooledThread(new Runnable() {
            @Override
            public void run() {
                startServerUnchecked();
            }
        });
    }

    private void startServerUnchecked() {
        mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP, "Start ADB server...");

        final ICommand command = new StartCommad();
        CommandExecute.execute(command.getCommand(mAdbPath));

        mHandler.sendEmptyMessage(CustomHandler.POST_GET_DEVICES);
    }

    private boolean canRun(){
        return !(isAdbEmpty() || mRunning);
    }

    private void addDeviceUnchecked(final String deviceId){
        mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP,"Connecting " + deviceId);

        final ICommand<String,String> command = new ConnectDevice(deviceId);
        final String result = CommandExecute.execute(command.getCommand(mAdbPath));
        final String message = command.parse(result);

        if (Utils.isBlank(message)) {
            Notify.error();
        }else {
            Notify.alert(message);
        }
    }

    public void getAllDevices(){
        if(!canRun()){
            return;
        }

        lock();

        runOnPooledThread(new Runnable() {
            @Override
            public void run() {
                getDevicesUnchecked();
            }
        });
    }

    public void disconnectRemoteDevice(final String deviceId){
        if (!canRun()){
            return;
        }

        if (!Utils.isRemoteDevice(deviceId)){
            Notify.error();
            return;
        }

        lock();

        runOnPooledThread(new Runnable() {
            @Override
            public void run() {
                mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP,"Disconnecting " + deviceId);

                final ICommand<String,String> command = new DisconnectRemoteDevice(deviceId);
                final String result = CommandExecute.execute(command.getCommand(mAdbPath));
                final String message = command.parse(result);

                if (Utils.isBlank(message)){
                    Notify.error();
                }else {
                    Notify.alert(message);
                }

                getDevicesUnchecked();
            }
        });
    }

    public void connectLocalDevice(final String deviceId){
        if (Utils.isBlank(deviceId)){
            Notify.error();
            getAllDevices();

            return;
        }

        lock();

        runOnPooledThread(new Runnable() {
            @Override
            public void run() {
                mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP,"Get ip address from " + deviceId);

                final ICommand<String,String> getIpCommand = new GainDeviceIP(deviceId);
                final String ipTmpResult = CommandExecute.execute(getIpCommand.getCommand(mAdbPath));
                final String ip = getIpCommand.parse(ipTmpResult);

                if (Utils.isBlank(ip)){
                    Notify.error(Utils.concat("Maybe target device [",deviceId,"] hasn't been connecting correct wifi"));
                    getDevicesUnchecked();
                    return;
                }

                final ICommand<?,?> alertAdbPort = new AlertAdbPort(deviceId);
                CommandExecute.execute(alertAdbPort.getCommand(mAdbPath));

                Notify.alert(Utils.concat("Now maybe you can disconnect the usb cable of target device [",deviceId,"]"));

                addDeviceUnchecked(Utils.concat(ip,":",Config.DEFAULT_PORT));

                mayWait();
                getDevicesUnchecked();
            }
        });
    }

    private void getDevicesUnchecked(){
        mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP,"Refresh devices list");

        final ICommand<String,Device[]> command = new AllDevices();
        final String result = CommandExecute.execute(command.getCommand(mAdbPath));
        final Device[] devices = command.parse(result);

        mHandler.sendMessage(CustomHandler.HANDLE_DEVICES, devices);
    }

    private void mayWait(){
        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void runOnPooledThread(@NotNull Runnable runnable){
        Executor.execute(runnable);
    }

    private void lock(){
        if(!mRunning) {
            mRunning = true;
            mViewLayer.showLoading();
        }
    }

    private void unlock(){
        if(mRunning) {
            mRunning = false;
            mViewLayer.hideLoading();
        }
    }

    class CustomHandler extends MainThreadHandler {
        static final int HANDLE_DEVICES = 1;
        static final int CHANGE_PROGRESS_TIP = 1 << 1;
        static final int POST_START_SERVER = 1 << 2;
        static final int POST_GET_DEVICES = 1 << 3;

        @Override
        protected void handleMessage(@NotNull Message msg) {
            final int what = msg.what;

            switch (what) {
                case HANDLE_DEVICES:
                    handleAllDevicesAction(msg);
                    unlock();
                    break;

                case CHANGE_PROGRESS_TIP:
                    handleChangeProgressTipAction(msg);
                    break;

                case POST_START_SERVER:
                    unlock();
                    startServer();
                    break;

                case POST_GET_DEVICES:
                    unlock();
                    getAllDevices();
                    break;

                default:
                    break;
            }
        }

        private void handleChangeProgressTipAction(@NotNull Message msg){
            final String newTip = msg.get();

            if(Utils.isBlank(newTip)){
                mViewLayer.refreshProgressTip(Config.DEFAULT_PROGRESS_TIP);
            }else {
                mViewLayer.refreshProgressTip(newTip);
            }
        }

        private void handleAllDevicesAction(@NotNull Message msg){
            final Device[] devices = msg.get();

            if (devices == null){
                mViewLayer.refreshDevices(null);
                return;
            }

            mViewLayer.refreshDevices(devices);
        }

    }

    public interface RootView{
        void onADBFail();
        void onADBSuccess(String path);
        void showLoading();
        void hideLoading();
        void refreshDevices(@Nullable Device[] devices);
        void refreshProgressTip(@NotNull String tip);
    }
}
