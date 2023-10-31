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
        setTitle("S-AES����");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());


        // ����һ�����ѡ���ı������ͱ�ǩ
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(3, 2));

        JLabel inputTypeLabel = new JLabel("��������:");
        inputType = new Choice();
        inputType.add("������");
        inputType.add("ASCII����");
        textPanel.add(inputTypeLabel);
        textPanel.add(inputType);


        JLabel inputLabel = new JLabel("���� (16λ):");
        inputText = new JTextField();
        JLabel keyLabel = new JLabel("��Կ (16λ):");
        keyText = new JTextField();

        textPanel.add(inputLabel);
        textPanel.add(inputText);
        textPanel.add(keyLabel);
        textPanel.add(keyText);

        // ���ı������ӵ������ı���
        panel.add(textPanel, BorderLayout.NORTH);

        // ����һ��������ڰ�װִ�н��ܺͷ��ذ�ť
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        executeButton = new JButton("ִ�н���");
        returnButton = new JButton("����");

        buttonPanel.add(executeButton);
        buttonPanel.add(returnButton);

        // ����ť�����ӵ�����������
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // ���������ʾ����
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        // �������ʾ������ӵ��������ϱ�
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // ִ�н��ܰ�ť�ļ�����
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ciphertext  = inputText.getText();
                String key = keyText.getText();
                String result = "";
                // ��ȡ��������
                String type = inputType.getSelectedItem();
                System.out.println(type);

                if ("������".equals(type)){
                    if (ciphertext.length() != 16 || key.length() != 32 || !Decrypt.isValidBinary(ciphertext) || !Decrypt.isValidBinary(key)) {
                        result = "��������Чλ�������ĺ���Կ��";
                    }
                    else {
                        // �����ĺ���Կת��Ϊ��������
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
                        //˫�ؽ���
                        int[] midtext = Decrypt.decrypt(ciphertextBits, key2);
                        midtext =Decrypt.decrypt(midtext,key1);
                         */
                        // ���ܣ����أ�
                        int[] midtext = Decrypt.decrypt(ciphertextBits, key1);
                        midtext =Decrypt.decrypt(midtext,key2);
                        int[] plaintextBits = Decrypt.decrypt(midtext,key1);

                        // �����ܽ��ת��Ϊ�ַ���
                        result = "���ܽ��: ";
                        for (int bit : plaintextBits) {
                            result += bit;
                        }
                        System.out.println(result);
                    }
                }
                else if ("ASCII����".equals(type)){
                    // ��ASCII����ת��Ϊ������
                    String binaryCiphertext = Decrypt.asciiToBinaryString(ciphertext);

                    // �����ĺ���Կת��Ϊ��������
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
                     // ʹ��S-AES�Ľ��ܷ�����˫�أ�
                    for (int i = 0; i < 16; i++) {
                        // ��ȡ��ǰ16λ����
                        int[] currentCiphertext = Arrays.copyOfRange(ciphertextBits, i * 16, (i + 1) * 16);

                        // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key2);

                        // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                    for (int i = 0; i < 16; i++) {
                        // ��һ�ν��ܺ������
                        int[] currentCiphertext = plaintextBits;

                        // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key1);

                        // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                     */

                    // ʹ��S-AES�Ľ��ܷ���
                    for (int i = 0; i < 16; i++) {
                        // ��ȡ��ǰ16λ����
                        int[] currentCiphertext = Arrays.copyOfRange(ciphertextBits, i * 16, (i + 1) * 16);

                        // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key1);

                        // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                    for (int i = 0; i < 16; i++) {
                        // ��һ�ν��ܺ������
                        int[] currentCiphertext = plaintextBits;

                        // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key2);

                        // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                    for (int i = 0; i < 16; i++) {
                        // �ڶ��ν��ܺ������
                        int[] currentCiphertext = plaintextBits;

                        // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key1);

                        // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                    // ��ӡ������ܽ��
                    result = "���ܽ��: "+ Encrypt.binaryStringToAscii(plaintextBits);
                }

                resultArea.setText(result);
            }


        });

        // ���ذ�ť�ļ�����
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ���ص� S_desWindow
                dispose();  // �رյ�ǰ����
                S_AESWindow sDesWindow = new S_AESWindow();
                sDesWindow.setVisible(true);  // �� S_desWindow
            }
        });

        getContentPane().add(panel);
    }
}
