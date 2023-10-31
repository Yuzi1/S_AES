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

        // ����һ��������ڰ�װִ�м��ܺͷ��ذ�ť
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        executeButton = new JButton("ִ�м���");
        JButton returnButton = new JButton("����");

        buttonPanel.add(executeButton);
        buttonPanel.add(returnButton);

        // ����ť�����ӵ�����������
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // ���������ʾ����
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        // �������ʾ������ӵ��������ϱ�
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // ִ�м��ܰ�ť�ļ�����
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String plaintext = inputText.getText();
                String key = keyText.getText();
                String result = "";


                // ��ȡ��������
                String type = inputType.getSelectedItem();
                System.out.println(type);

                if ("������".equals(type)) {
                    // �����������ĺ���Կ�Ƿ���16λ��16λ�Ķ������ַ���
                    if (plaintext.length() != 16 || key.length() != 32 || !Encrypt.isValidBinary(plaintext) || !Encrypt.isValidBinary(key)) {
                        result = "��������Чλ�������ĺ���Կ��";
                    }
                    else {
                        // �����ĺ���Կת��Ϊ��������
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
                        //˫�ؼ���
                        int[] midtext = Encrypt.encrypt(plaintextBits, key1);
                        int[] ciphertextBits = Encrypt.encrypt(midtext,key2);
                         */

                        // ʹ��S-DES�ļ��ܷ��������أ�
                        int[] midtext = Encrypt.encrypt(plaintextBits, key1);
                        int[] ciphertextBits = Encrypt.encrypt(midtext,key2);
                        ciphertextBits = Encrypt.encrypt(ciphertextBits,key1);

                        // �����ܽ��ת��Ϊ�ַ���
                        result = "���ܽ��: ";
                        for (int bit : ciphertextBits) {
                            result += bit;
                        }

                    }
                }
                else if ("ASCII����".equals(type)){
                    // ��ASCII����ת��Ϊ������
                    String binaryPlaintext = Encrypt.asciiToBinaryString(plaintext);

                    if (binaryPlaintext.length() != 16*16 || key.length() != 32 || !Encrypt.isValidBinary(binaryPlaintext) || !Encrypt.isValidBinary(key)){
                        result = "��������Чλ�������ĺ���Կ��";
                    }
                    else{
                        // �����ĺ���Կת��Ϊ��������
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
                        // ʹ��S-AES�ļ��ܷ�����˫�ؼ��ܣ�
                        for (int i = 0; i < 16; i++) {
                            // ��ȡ��ǰ16λ����
                            int[] currentPlaintext = Arrays.copyOfRange(plaintextBits, i * 16, (i + 1) * 16);

                            // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key1);

                            // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }

                        for (int i = 0; i < 16; i++) {
                            // ��һ�μ��ܺ������
                            int[] currentPlaintext = ciphertextBits;

                            // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key2);

                            // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }
                         */

                        // ʹ��S-AES�ļ��ܷ���
                        for (int i = 0; i < 16; i++) {
                            // ��ȡ��ǰ16λ����
                            int[] currentPlaintext = Arrays.copyOfRange(plaintextBits, i * 16, (i + 1) * 16);

                            // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key1);

                            // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }

                        for (int i = 0; i < 16; i++) {
                            // ��һ�μ��ܺ������
                            int[] currentPlaintext = ciphertextBits;

                            // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key2);

                            // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }
                        for (int i = 0; i < 16; i++) {
                            // �ڶ��μ��ܺ������
                            int[] currentPlaintext = ciphertextBits;

                            // ִ��S-AES���ܲ����������ܽ���洢��encryptedBits�е���Ӧλ��
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key1);

                            // ����ǰ���ܽ���洢��������ܽ�������е���Ӧλ��
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }
                        // ��ӡ������ܽ��
                        result = "���ܽ��: "+ Encrypt.binaryStringToAscii(ciphertextBits);
                    }

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
