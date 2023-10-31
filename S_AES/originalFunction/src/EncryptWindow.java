import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class EncryptWindow extends JFrame {
    private JTextField inputText;
    private JTextField keyText;
    private JButton executeButton;
    private JTextArea resultArea;
    //private Choice inputType;

    public EncryptWindow() {
        setTitle("S-AES加密");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());


        // 创建一个面板选择、文本输入框和标签
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(3, 2));


        JLabel inputLabel = new JLabel("明文 (16位):");
        inputText = new JTextField();
        JLabel keyLabel = new JLabel("密钥 (16位):");
        keyText = new JTextField();

        textPanel.add(inputLabel);
        textPanel.add(inputText);
        textPanel.add(keyLabel);
        textPanel.add(keyText);

        // 将文本面板添加到主面板的北边
        panel.add(textPanel, BorderLayout.NORTH);

        // 创建一个面板用于包装执行加密和返回按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        executeButton = new JButton("执行加密");
        JButton returnButton = new JButton("返回");

        buttonPanel.add(executeButton);
        buttonPanel.add(returnButton);

        // 将按钮面板添加到主面板的中央
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 创建结果显示区域
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        // 将结果显示区域添加到主面板的南边
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // 执行加密按钮的监听器
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String plaintext = inputText.getText();
                String key = keyText.getText();
                String result = "";

/*
                // 获取输入类型
                String type = inputType.getSelectedItem();
                System.out.println(type);


 */
                // 检查输入的明文和密钥是否是8位和10位的二进制字符串
                if (plaintext.length() != 16 || key.length() != 16 || !Encrypt.isValidBinary(plaintext) || !Encrypt.isValidBinary(key)) {
                    result = "请输入有效位数的明文和密钥。";
                } else {
                    // 将明文和密钥转换为整数数组
                    int[] plaintextBits = new int[16];
                    int[] keyBits = new int[16];
                    for (int i = 0; i < 16; i++) {
                        plaintextBits[i] = Integer.parseInt(Character.toString(plaintext.charAt(i)));
                    }
                    for (int i = 0; i < 16; i++) {
                        keyBits[i] = Integer.parseInt(Character.toString(key.charAt(i)));
                    }

                    // 使用S-DES的加密方法
                    int[] ciphertextBits = Encrypt.encrypt(plaintextBits, keyBits);

                    // 将加密结果转换为字符串
                    result = "加密结果: ";
                    for (int bit : ciphertextBits) {
                        result += bit;
                    }

                }

                resultArea.setText(result);
            }
        });

        // 返回按钮的监听器
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 返回到 S_desWindow
                dispose();  // 关闭当前窗口
                S_AESWindow sDesWindow = new S_AESWindow();
                sDesWindow.setVisible(true);  // 打开 S_desWindow
            }
        });

        getContentPane().add(panel);
    }

}
