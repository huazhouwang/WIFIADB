package adb.wifi.woaiwhz.base;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by wanghuazhou on 01/01/2017.
 */
public class Global {
    private Project mThisProject;
    private String mADBPath;

    public void setADBPath(String adbPath) {
        mADBPath = adbPath;
    }

    public String adbPath() {
        return mADBPath;
    }

    public void bindProject(@NotNull final Project project) {
        mThisProject = project;
    }

    @NotNull
    public Project project() {
        if (mThisProject == null) {
            throw new NullPointerException("You should bind project first");
        }

        return mThisProject;
    }

    Global(){}

    @NotNull
    public static Global instance() {
        return Holder.INSTANCE;
    }

    static class Holder {
        final static Global INSTANCE = new Global();
    }
}
