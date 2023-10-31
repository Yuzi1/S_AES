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
        setTitle("S-AES����");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());


        // ����һ�����ѡ���ı������ͱ�ǩ
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(3, 2));


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

/*
                // ��ȡ��������
                String type = inputType.getSelectedItem();
                System.out.println(type);


 */
                // �����������ĺ���Կ�Ƿ���8λ��10λ�Ķ������ַ���
                if (plaintext.length() != 16 || key.length() != 16 || !Encrypt.isValidBinary(plaintext) || !Encrypt.isValidBinary(key)) {
                    result = "��������Чλ�������ĺ���Կ��";
                } else {
                    // �����ĺ���Կת��Ϊ��������
                    int[] plaintextBits = new int[16];
                    int[] keyBits = new int[16];
                    for (int i = 0; i < 16; i++) {
                        plaintextBits[i] = Integer.parseInt(Character.toString(plaintext.charAt(i)));
                    }
                    for (int i = 0; i < 16; i++) {
                        keyBits[i] = Integer.parseInt(Character.toString(key.charAt(i)));
                    }

                    // ʹ��S-DES�ļ��ܷ���
                    int[] ciphertextBits = Encrypt.encrypt(plaintextBits, keyBits);

                    // �����ܽ��ת��Ϊ�ַ���
                    result = "���ܽ��: ";
                    for (int bit : ciphertextBits) {
                        result += bit;
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
