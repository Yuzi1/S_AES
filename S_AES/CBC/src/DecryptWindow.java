import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class DecryptWindow extends JFrame {
    private JTextField inputText;
    private JTextField keyText;
    private JButton executeButton;
    private JButton returnButton;
    private JTextArea resultArea;
    private Choice inputType;

    public DecryptWindow() {
        setTitle("S-AES解密");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());


        // 创建一个面板选择、文本输入框和标签
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(3, 2));

        JLabel inputTypeLabel = new JLabel("输入类型:");
        inputType = new Choice();
        inputType.add("二进制");
        inputType.add("ASCII编码");
        textPanel.add(inputTypeLabel);
        textPanel.add(inputType);


        JLabel inputLabel = new JLabel("密文 (16位):");
        inputText = new JTextField();
        JLabel keyLabel = new JLabel("密钥 (16位):");
        keyText = new JTextField();

        textPanel.add(inputLabel);
        textPanel.add(inputText);
        textPanel.add(keyLabel);
        textPanel.add(keyText);

        // 将文本面板添加到主面板的北边
        panel.add(textPanel, BorderLayout.NORTH);

        // 创建一个面板用于包装执行解密和返回按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        executeButton = new JButton("执行解密");
        returnButton = new JButton("返回");

        buttonPanel.add(executeButton);
        buttonPanel.add(returnButton);

        // 将按钮面板添加到主面板的中央
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 创建结果显示区域
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        // 将结果显示区域添加到主面板的南边
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // 执行解密按钮的监听器
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ciphertext  = inputText.getText();
                String key = keyText.getText();
                String result = "";
                // 获取输入类型
                String type = inputType.getSelectedItem();
                System.out.println(type);

                if ("二进制".equals(type)){
                    if (ciphertext.length() != 16 || key.length() != 32 || !Decrypt.isValidBinary(ciphertext) || !Decrypt.isValidBinary(key)) {
                        result = "请输入有效位数的密文和密钥。";
                    }
                    else {
                        // 将密文和密钥转换为整数数组
                        int[] ciphertextBits = new int[16];
                        int[] keyBits = new int[32];
                        for (int i = 0; i < 16; i++) {
                            ciphertextBits[i] = Integer.parseInt(Character.toString(ciphertext.charAt(i)));
                        }
                        for (int i = 0; i < 32; i++) {
                            keyBits[i] = Integer.parseInt(Character.toString(key.charAt(i)));
                        }

                        int[] key1 = Arrays.copyOfRange(keyBits,0,16);
                        int[] key2 = Arrays.copyOfRange(keyBits,16,32);

                        /*
                        //双重解密
                        int[] midtext = Decrypt.decrypt(ciphertextBits, key2);
                        midtext =Decrypt.decrypt(midtext,key1);
                         */
                        // 解密（三重）
                        int[] midtext = Decrypt.decrypt(ciphertextBits, key1);
                        midtext =Decrypt.decrypt(midtext,key2);
                        int[] plaintextBits = Decrypt.decrypt(midtext,key1);

                        // 将解密结果转换为字符串
                        result = "解密结果: ";
                        for (int bit : plaintextBits) {
                            result += bit;
                        }
                        System.out.println(result);
                    }
                }
                else if ("ASCII编码".equals(type)){
                    // 将ASCII编码转换为二进制
                    String binaryCiphertext = Decrypt.asciiToBinaryString(ciphertext);

                    // 将密文和密钥转换为整数数组
                    int[] ciphertextBits = new int[16*16];
                    int[] keyBits = new int[16];

                    for (int i = 0; i < 16*16; i++) {
                        ciphertextBits[i] = Integer.parseInt(Character.toString(binaryCiphertext.charAt(i)));
                    }
                    for (int i = 0; i < 32; i++) {
                        keyBits[i] = Integer.parseInt(Character.toString(key.charAt(i)));
                    }


                    int[] key1 = Arrays.copyOfRange(keyBits,0,16);
                    int[] key2 = Arrays.copyOfRange(keyBits,16,32);

                    int[] plaintextBits = new int[16 * 16];

                    /*
                     // 使用S-AES的解密方法（双重）
                    for (int i = 0; i < 16; i++) {
                        // 提取当前16位密文
                        int[] currentCiphertext = Arrays.copyOfRange(ciphertextBits, i * 16, (i + 1) * 16);

                        // 执行S-AES解密操作，将加密结果存储在encryptedBits中的相应位置
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key2);

                        // 将当前加密结果存储在整体加密结果数组中的相应位置
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                    for (int i = 0; i < 16; i++) {
                        // 第一次解密后的密文
                        int[] currentCiphertext = plaintextBits;

                        // 执行S-AES解密操作，将加密结果存储在encryptedBits中的相应位置
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key1);

                        // 将当前加密结果存储在整体加密结果数组中的相应位置
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                     */

                    // 使用S-AES的解密方法
                    for (int i = 0; i < 16; i++) {
                        // 提取当前16位密文
                        int[] currentCiphertext = Arrays.copyOfRange(ciphertextBits, i * 16, (i + 1) * 16);

                        // 执行S-AES解密操作，将加密结果存储在encryptedBits中的相应位置
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key1);

                        // 将当前加密结果存储在整体加密结果数组中的相应位置
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                    for (int i = 0; i < 16; i++) {
                        // 第一次解密后的密文
                        int[] currentCiphertext = plaintextBits;

                        // 执行S-AES解密操作，将加密结果存储在encryptedBits中的相应位置
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key2);

                        // 将当前加密结果存储在整体加密结果数组中的相应位置
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                    for (int i = 0; i < 16; i++) {
                        // 第二次解密后的密文
                        int[] currentCiphertext = plaintextBits;

                        // 执行S-AES解密操作，将加密结果存储在encryptedBits中的相应位置
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key1);

                        // 将当前加密结果存储在整体加密结果数组中的相应位置
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                    // 打印整体加密结果
                    result = "解密结果: "+ Encrypt.binaryStringToAscii(plaintextBits);
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
