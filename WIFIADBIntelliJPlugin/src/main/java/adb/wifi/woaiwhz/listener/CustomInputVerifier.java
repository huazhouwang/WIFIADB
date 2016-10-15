package adb.wifi.woaiwhz.listener;

import adb.wifi.woaiwhz.base.Notify;
import org.apache.http.util.TextUtils;

import javax.swing.*;

/**
 * Created by huazhou.whz on 2016/10/7.
 */
public class CustomInputVerifier extends InputVerifier {
    private final int mMaxNumber;
    private final int mDefaultNumber;

    /**
     * 0 <= input < maxNumber
     */
    public CustomInputVerifier(int maxNumber){
        mMaxNumber = maxNumber;
        mDefaultNumber = Integer.MIN_VALUE;
    }

    public CustomInputVerifier(int maxNumber,int defaultNumber) {
        mMaxNumber = maxNumber;
        mDefaultNumber = defaultNumber;
    }

    @Override
    public boolean verify(JComponent input) {
        if(!(input instanceof JTextField)){
            Notify.error();
            return false;
        }

        final JTextField current = (JTextField) input;
        final String text = current.getText();

        if(TextUtils.isBlank(text)){
            if(mDefaultNumber != Integer.MIN_VALUE){
                current.setText(String.valueOf(mDefaultNumber));
            }
            return true;
        }

        try {
            final int number = Integer.valueOf(text);
            if(number >= 0 && number < mMaxNumber){
                return true;
            }else {
                Notify.error("Number should >= 0 and &lt " + mMaxNumber);
                return false;
            }
        }catch (NumberFormatException e){
            Notify.error("error input : " + text);
            return false;
        }
    }
}
