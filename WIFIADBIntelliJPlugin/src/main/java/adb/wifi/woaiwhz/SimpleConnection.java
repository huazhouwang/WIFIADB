package adb.wifi.woaiwhz;

import adb.wifi.woaiwhz.base.Action;
import adb.wifi.woaiwhz.component.CommandExecute;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import org.apache.http.util.TextUtils;
import org.jetbrains.android.sdk.AndroidSdkUtils;

import java.io.File;

/**
 * Created by huazhou.whz on 2016/9/27.
 */
public class SimpleConnection extends AnAction {

    public SimpleConnection(){
        super("WIFI ADB");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        if(AndroidSdkUtils.isAndroidSdkAvailable()) {
            Messages.showInputDialog(project, "text device ip", "WIFI ADB", Messages.getQuestionIcon(), null, new SimpleInputValidator(project));
        }else {
            Messages.showMessageDialog(project,"fail to find android sdk from this project","WIFI ADB", Messages.getErrorIcon());
        }
    }

    private static class SimpleInputValidator implements InputValidator{
        private Project mProject;
        private String mAdbPath;

        private SimpleInputValidator(Project project) {
            mProject = project;
            mAdbPath = getAdbPath(project);
        }

        @Override
        public boolean checkInput(String inputString) {
            return true;
        }

        @Override
        public boolean canClose(String inputString) {
            String result = CommandExecute.execute(mAdbPath + Action.SPACE + Action.CONNECT + Action.SPACE + inputString);

            if(!TextUtils.isEmpty(result) && result.contains(Action.CONNECT)){
                Messages.showMessageDialog(mProject,"connect to " + inputString,"WIFI ADB",Messages.getInformationIcon());
                return true;
            }else {
                Messages.showMessageDialog(mProject,"fail = " + inputString,"WIFI ADB",Messages.getErrorIcon());
                return false;
            }
        }
    }

    private static String getAdbPath(Project project) {
        String adbPath = "";
        File adbFile = AndroidSdkUtils.getAdb(project);
        if (adbFile != null) {
            adbPath = adbFile.getAbsolutePath();
        }
        return adbPath;
    }
}
