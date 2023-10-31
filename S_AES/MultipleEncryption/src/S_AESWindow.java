import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class S_AESWindow extends JFrame{
    private JButton encryptButton;
    private JButton decryptButton;

    public S_AESWindow() {
        setTitle("S-AES");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER,40,40)); // 使用BoxLayout布局，垂直排列

        JLabel jLabel = new JLabel("S-AES加密解密工具");
        jLabel.setForeground(new Color(000000));
        jLabel.setFont(new Font("黑体",Font.PLAIN,30));
        jLabel.setBounds(100,50,300,70);

        panel.add(jLabel);

        //加密、解密按钮
        encryptButton = new JButton("加密功能");
        decryptButton = new JButton("解密功能");



        panel.add(encryptButton);
        panel.add(decryptButton);

        getContentPane().add(panel, BorderLayout.CENTER);

        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEncryptWindow();
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDecryptWindow();
            }
        });
    }

    private void openEncryptWindow() {
        EncryptWindow encryptWindow = new EncryptWindow();
        encryptWindow.setVisible(true);
        dispose();
    }

    private void openDecryptWindow() {
        DecryptWindow decryptWindow = new DecryptWindow();
        decryptWindow.setVisible(true);
        dispose();
    }
}
