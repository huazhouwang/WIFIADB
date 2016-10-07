package adb.wifi.woaiwhz.base;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by huazhou.whz on 2016/10/7.
 */
public class NumberDocumentFilter extends PlainDocument implements KeyListener{
    private final int mLength;

    public NumberDocumentFilter(int length) {
        mLength = length;
    }

    public void insertString(int offs, String source, AttributeSet a) throws BadLocationException {
        final int oldLength = getLength();
        String newString = "";

        if(oldLength < mLength){
            newString = source;
        }

        super.insertString(offs, newString, a);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        final int key = e.getKeyChar();

        if(key < KeyEvent.VK_0 || key > KeyEvent.VK_9){
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
