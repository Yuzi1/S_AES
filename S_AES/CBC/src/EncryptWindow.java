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
    private Choice inputType;

    public EncryptWindow() {
        setTitle("S-AES加密");
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


                // 获取输入类型
                String type = inputType.getSelectedItem();
                System.out.println(type);

                if ("二进制".equals(type)) {
                    // 检查输入的明文和密钥是否是16位和16位的二进制字符串
                    if (plaintext.length() != 16 || key.length() != 32 || !Encrypt.isValidBinary(plaintext) || !Encrypt.isValidBinary(key)) {
                        result = "请输入有效位数的明文和密钥。";
                    }
                    else {
                        // 将明文和密钥转换为整数数组
                        int[] plaintextBits = new int[16];
                        int[] keyBits = new int[32];
                        for (int i = 0; i < 16; i++) {
                            plaintextBits[i] = Integer.parseInt(Character.toString(plaintext.charAt(i)));
                        }
                        for (int i = 0; i < 32; i++) {
                            keyBits[i] = Integer.parseInt(Character.toString(key.charAt(i)));
                        }

                        int[] key1 = Arrays.copyOfRange(keyBits,0,16);
                        int[] key2 = Arrays.copyOfRange(keyBits,16,32);

                        /*
                        //双重加密
                        int[] midtext = Encrypt.encrypt(plaintextBits, key1);
                        int[] ciphertextBits = Encrypt.encrypt(midtext,key2);
                         */

                        // 使用S-DES的加密方法（三重）
                        int[] midtext = Encrypt.encrypt(plaintextBits, key1);
                        int[] ciphertextBits = Encrypt.encrypt(midtext,key2);
                        ciphertextBits = Encrypt.encrypt(ciphertextBits,key1);

                        // 将加密结果转换为字符串
                        result = "加密结果: ";
                        for (int bit : ciphertextBits) {
                            result += bit;
                        }

                    }
                }
                else if ("ASCII编码".equals(type)){
                    // 将ASCII编码转换为二进制
                    String binaryPlaintext = Encrypt.asciiToBinaryString(plaintext);

                    if (binaryPlaintext.length() != 16*16 || key.length() != 32 || !Encrypt.isValidBinary(binaryPlaintext) || !Encrypt.isValidBinary(key)){
                        result = "请输入有效位数的明文和密钥。";
                    }
                    else{
                        // 将明文和密钥转换为整数数组
                        int[] plaintextBits = new int[16*16];
                        int[] keyBits = new int[32];


                        for (int i = 0; i < 16*16; i++) {
                            plaintextBits[i] = Integer.parseInt(Character.toString(binaryPlaintext.charAt(i)));
                        }
                        for (int i = 0; i < 32; i++) {
                            keyBits[i] = Integer.parseInt(Character.toString(key.charAt(i)));
                        }

                        int[] key1 = Arrays.copyOfRange(keyBits,0,16);
                        int[] key2 = Arrays.copyOfRange(keyBits,16,32);
                        int[] ciphertextBits = new int[16 * 16];


                        /*
                        // 使用S-AES的加密方法（双重加密）
                        for (int i = 0; i < 16; i++) {
                            // 提取当前16位明文
                            int[] currentPlaintext = Arrays.copyOfRange(plaintextBits, i * 16, (i + 1) * 16);

                            // 执行S-AES加密操作，将加密结果存储在encryptedBits中的相应位置
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key1);

                            // 将当前加密结果存储在整体加密结果数组中的相应位置
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }

                        for (int i = 0; i < 16; i++) {
                            // 第一次加密后的明文
                            int[] currentPlaintext = ciphertextBits;

                            // 执行S-AES加密操作，将加密结果存储在encryptedBits中的相应位置
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key2);

                            // 将当前加密结果存储在整体加密结果数组中的相应位置
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }
                         */

                        // 使用S-AES的加密方法
                        for (int i = 0; i < 16; i++) {
                            // 提取当前16位明文
                            int[] currentPlaintext = Arrays.copyOfRange(plaintextBits, i * 16, (i + 1) * 16);

                            // 执行S-AES加密操作，将加密结果存储在encryptedBits中的相应位置
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key1);

                            // 将当前加密结果存储在整体加密结果数组中的相应位置
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }

                        for (int i = 0; i < 16; i++) {
                            // 第一次加密后的明文
                            int[] currentPlaintext = ciphertextBits;

                            // 执行S-AES加密操作，将加密结果存储在encryptedBits中的相应位置
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key2);

                            // 将当前加密结果存储在整体加密结果数组中的相应位置
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }
                        for (int i = 0; i < 16; i++) {
                            // 第二次加密后的明文
                            int[] currentPlaintext = ciphertextBits;

                            // 执行S-AES加密操作，将加密结果存储在encryptedBits中的相应位置
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key1);

                            // 将当前加密结果存储在整体加密结果数组中的相应位置
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }
                        // 打印整体加密结果
                        result = "加密结果: "+ Encrypt.binaryStringToAscii(ciphertextBits);
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
