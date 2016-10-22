package adb.wifi.woaiwhz.presenter;

import adb.wifi.woaiwhz.base.CommandExecute;
import adb.wifi.woaiwhz.base.Config;
import adb.wifi.woaiwhz.base.Notify;
import adb.wifi.woaiwhz.base.Utils;
import adb.wifi.woaiwhz.base.device.Device;
import adb.wifi.woaiwhz.command.*;
import adb.wifi.woaiwhz.dispatch.Executor;
import adb.wifi.woaiwhz.dispatch.Handler;
import adb.wifi.woaiwhz.dispatch.Message;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by huazhou.whz on 2016/10/14.
 */
public class RootPresenter {
    private final RootView mViewLayer;
    private final Handler mHandler;

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
            mViewLayer.onADBEmpty();
        }else {
            mViewLayer.onADBComplete(mAdbPath);
        }
    }

    private boolean isAdbEmpty(){
        return Utils.isBlank(mAdbPath);
    }

    public void addDevice(final String deviceId){
        if(Utils.isBlank(deviceId)){
            return;
        }

        lock();

        runOnPooledThread(() ->{
            addDeviceInCurrentThread(deviceId);

            getDevicesInCurrentThread();
        });
    }

    private void addDeviceInCurrentThread(final String deviceId){
        mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP,"Connecting " + deviceId);

        final ICommand<String,String> command = new ConnectDevice(deviceId);
        final String result = CommandExecute.execute(command.getCommand(mAdbPath));
        final String message = command.parse(result);
        Notify.alert(message);
    }
    
    public void getAllDevices(){
        if(isAdbEmpty() && !isRunning()){
            return;
        }

        lock();

        runOnPooledThread(this::getDevicesInCurrentThread);
    }

    public void disconnectRemoteDevice(final String deviceId){
        if (!Utils.isRemoteDevice(deviceId)){
            Notify.error();
            getAllDevices();
            return;
        }

        lock();

        runOnPooledThread(() ->{
            mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP,"Disconnecting " + deviceId);

            final ICommand<String,String> command = new DisconnectRemoteDevice(deviceId);
            final String result = CommandExecute.execute(command.getCommand(mAdbPath));
            final String message = command.parse(result);
            Notify.alert(message);

            getDevicesInCurrentThread();
        });
    }

    public void connectLocalDevice(final String deviceId){
        if (Utils.isBlank(deviceId)){
            Notify.error();
            getAllDevices();

            return;
        }

        lock();

        runOnPooledThread(() -> {
            mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP,"Get ip address from " + deviceId);

            final ICommand<String,String> getIpCommand = new GainDeviceIP(deviceId);
            final String ipTmpResult = CommandExecute.execute(getIpCommand.getCommand(mAdbPath));
            final String ip = getIpCommand.parse(ipTmpResult);

            if (Utils.isBlank(ip)){
                Notify.error(Utils.concat("Maybe target device [",deviceId,"] hasn't connected correct wifi"));
                return;
            }

            final ICommand<?,?> alertAdbPort = new AlertAdbPort(deviceId);
            CommandExecute.execute(alertAdbPort.getCommand(mAdbPath));

            Notify.alert(Utils.concat("Now maybe you can disconnect the usb cable of target device [",deviceId,"]"));

            addDeviceInCurrentThread(Utils.concat(ip,":",Config.DEFAULT_PORT));

            mockWait();
            getDevicesInCurrentThread();
        });
    }

    private void getDevicesInCurrentThread(){
        mHandler.sendMessage(CustomHandler.CHANGE_PROGRESS_TIP,"Refresh devices list");

        final ICommand<String,Device[]> command = new AllDevices();
        final String result = CommandExecute.execute(command.getCommand(mAdbPath));
        final Device[] devices = command.parse(result);

        final Message message = new Message(CustomHandler.GET_ALL_DEVICES,devices);
        mHandler.sendMessage(message);
    }

    private void mockWait(){
        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void runOnPooledThread(@NotNull Runnable runnable){
        Executor.execute(runnable);
    }

    private boolean isRunning(){
        return mRunning;
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

    private class CustomHandler extends Handler {
        private static final int GET_ALL_DEVICES = 1;
        private static final int CHANGE_PROGRESS_TIP = 1 << 1;

        @Override
        protected void handleMessage(@NotNull Message msg) {
            final int what = msg.what;

            switch (what) {
                case GET_ALL_DEVICES:
                    handleAllDevicesAction(msg);
                    unlock();
                    break;

                case CHANGE_PROGRESS_TIP:
                    handleChangeProgressTipAction(msg);
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
        void onADBEmpty();
        void onADBComplete(String path);
        void showLoading();
        void hideLoading();
        void refreshDevices(@Nullable Device[] devices);
        void refreshProgressTip(@NotNull String tip);
    }
}
