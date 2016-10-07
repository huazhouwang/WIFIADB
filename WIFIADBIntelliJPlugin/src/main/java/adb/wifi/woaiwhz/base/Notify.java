package adb.wifi.woaiwhz.base;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * Created by huazhou.whz on 2016/10/7.
 */
public class Notify {
    private static final NotificationGroup Builder = NotificationGroup.balloonGroup(Config.TITLE);

    public static void alert(String line){
        Notification notification =
                Builder.createNotification(Config.TITLE, line, NotificationType.INFORMATION, null);

        notify(notification);
    }


    public static void error(){
        error("Something wrong");
    }

    public static void warn(String warning){
        Notification notification =
                Builder.createNotification(Config.TITLE, warning, NotificationType.WARNING, null);

        notify(notification);
    }

    public static void error(String error){
        Notification notification =
                Builder.createNotification(Config.TITLE, error, NotificationType.ERROR, null);

        notify(notification);
    }

    private static void notify(final Notification notification){
        Executor.post(new Runnable() {
            @Override
            public void run() {
                Notifications.Bus.notify(notification);
            }
        });
    }
}
