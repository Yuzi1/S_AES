import javax.swing.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                S_AESWindow win = new S_AESWindow();
                win.setVisible(true);
            }
        });


    }
}