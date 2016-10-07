package adb.wifi.woaiwhz.base;

import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

/**
 * Created by huazhou.whz on 2016/10/7.
 */
public class Executor {
    /**
     * execute in AWT event dispatching thread
     */
    public static void post(@NotNull final Runnable runnable){
        ApplicationManager.getApplication().invokeLater(runnable);
    }

    /**
     * execute in pooled thread
     */
    public static void execute(@NotNull final Runnable runnable){
        ApplicationManager.getApplication().executeOnPooledThread(runnable);
    }
}
