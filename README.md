# S_AES
1 作业任务：根据"信息安全导论"课程第8-9次课讲述的AES算法，在课外认真阅读教科书附录D的内容，学习了解S-AES算法，并使用你们自己最擅长的程序语言(C++/QT或Java+Swing、Python+QT等)来编程实现加、解密程序。
2编程和测试
2.1第1关：基本测试      
根据S-AES算法编写和调试程序，提供GUI解密支持用户交互。输入可以是16bit的数据和16bit的密钥，输出是16bit的密文。
加密代码：
```Java
import java.util.Arrays;

public class Encrypt {
    private static int[] w2;
    private static int[] w3;
    private static int[] w4;
    private static int[] w5;

    //S盒（4x4半字节值矩阵），包含所有4位值的排列
    static int[][] SBOX = {
            {9,4,10,11},
            {13,1,8,5},
            {6,2,0,3},
            {12,14,15,7}
    };
    static int[][] mix = {
            {1,4},
            {4,1}
    };
    //异或函数
    public static int[] exclusiveOR(int[] array1, int[] array2){

        int[] result = new int[array1.length];
        for (int i = 0; i < array1.length; i++){
            result[i] = array1[i] ^ array2[i];

        }
        return result;
    }

    //SBox查表
    private static int checkSBox(int[] text){
        int row = text[0]*2 + text[1];
        int col = text[2]*2 + text[3];
        int num = SBOX[row][col];
        return num;
    }

    //整数转换为二进制
    public static int[] IntToBinary(int num){
        int[] temp = new int[4];
        for (int k = 3; k >= 0; k--){
            temp[k] = num & 1;
            num >>= 1;
        }
        return temp;
    }
    //半字节表示
    public static int[][] HalfBytes(int[] text) {
        int[][] S = new int[2][2];

        S[0][0] = (text[0] << 3) | (text[1] << 2) | (text[2] << 1) | text[3];
        S[0][1] = (text[8] << 3) | (text[9] << 2) | (text[10] << 1) | text[11];
        S[1][0] = (text[4] << 3) | (text[5] << 2) | (text[6] << 1) | text[7];
        S[1][1] = (text[12] << 3) | (text[13] << 2) | (text[14] << 1) | text[15];

        return S;
    }


    //交换函数
    public static int[] swapAndMerge(int[] array1, int[] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("数组长度不同.");
        }

        int[] mergedArray ; // 创建一个新的数组，长度为两个输入数组的长度之和

        int[] temp = array1;
        array1 = array2;
        array2 = temp;
        // 复制 array1 和 array2 到 mergedArray
        mergedArray = Merge(array1,array2);
        return mergedArray; // 返回合并后的数组
    }

    //合并函数
    public static int[] Merge(int[] array1, int[] array2){
        int[] mergedArray = new int[array1.length * 2]; // 创建一个新的数组，长度为两个输入数组的长度之和

        // 复制 array1 和 array2 到 mergedArray
        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);
        return mergedArray;

    }
    //密钥扩展：16位分为两个8位字密钥，
    public static int[] SubNib(int[] rotnib) {
        // 在这里执行SubNib操作
        int[] N0 = Arrays.copyOfRange(rotnib,0,4);
        int[] N1 = Arrays.copyOfRange(rotnib,4,8);


        int num0 = checkSBox(N0);
        N0 = IntToBinary(num0);

        int num1 = checkSBox(N1);
        N1 = IntToBinary(num1);

        int[] result  = Merge(N0,N1);


        return result;
    }


    public static int[] RotNib(int[] rcon) {
        int[] rotnib;
        // 在这里执行RotNib操作
        int[] N0 = Arrays.copyOfRange(rcon,0,4);
        int[] N1 = Arrays.copyOfRange(rcon,4,8);

        rotnib = swapAndMerge(N0,N1);
        return rotnib;
    }

    public static int[] Rcon(int round) {
        int[] rcon = null;
        if(round == 1){
            rcon = new int[]{1, 0, 0, 0, 0, 0, 0, 0};
        }
        else if (round == 2){
            rcon = new int[]{0,0,1,1,0,0,0,0};
        }
        return rcon;
    }
    public static int[] g(int[] w,int i){
        int[] rotnib = RotNib(w);
        int[] subnib = SubNib(rotnib);
        int[] rcon = Rcon(i);
        int[] reslut = exclusiveOR(rcon,subnib);
        return reslut;
    }
    public static void KeyExpansion(int[] key){

        int[] w0 = Arrays.copyOfRange(key,0,8);
        int[] w1 = Arrays.copyOfRange(key,8,16);

        int[] g1 = g(w1,1);
        w2 = exclusiveOR(w0,g1);
        w3 = exclusiveOR(w2,w1);


        int[] g2 = g(w3,2);
        w4 = exclusiveOR(w2,g2);
        w5 = exclusiveOR(w4,w3);

    }


    //密钥加函数Ak（逆函数是本身）
    private static int[][] Ak(int[][] plaintext, int[][] key){
        int[][] reslut = new int[2][2];

        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 2; j++){
                reslut[i][j] =plaintext[i][j]^key[i][j];
            }
        }
        return reslut;
    }

    //半字节代替NS
    private static int[][] NS(int[][] ak){
        int[][] reslut = new int[2][2];
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 2; j++){
                int[] temp = IntToBinary(ak[i][j]);
                reslut[i][j] = checkSBox(temp);
            }
        }
        return reslut;
    }

    //行移位SR
    private static int[][] SR(int[][] halfBytes){
        int[][] result = halfBytes;
        int temp = halfBytes[1][0];
        halfBytes[1][0] = halfBytes[1][1];
        halfBytes[1][1] = temp;
        return result;
    }

    //列混淆MC,矩阵乘法
    public static int[][] MC(int[][] sr){
        int[][] result = new int[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j] = multiplyGF4(mix[i][0], sr[0][j]) ^ multiplyGF4(mix[i][1], sr[1][j]);
            }
        }

        return result;
    }
    public static int multiplyGF4(int a, int b) {
        int product = 0;

        while (a > 0) {
            if ((a & 1) == 1) {
                product ^= b;
            }

            boolean carry = (b & 8) != 0;
            b = (b << 1) & 15;

            if (carry) {
                b ^= 3;
            }

            a = a >> 1;
        }

        return product;
    }

    //加密
    public static  int[] encrypt(int[] plaintext, int[] key){
        int[] ciphertext;
        int[][] Plaintext = HalfBytes(plaintext);

        //扩展密钥
        KeyExpansion(key);
        int[][] Key1 = HalfBytes(key);
        int[][] Key2 =HalfBytes( Merge(w2,w3));
        int[][] Key3 = HalfBytes(Merge(w4,w5));

        //第一次轮密钥加
        int[][] ak = Ak(Plaintext,Key1);

        //第一轮半字节代替
        int[][] ns = NS(ak);


        //第一轮行位移
        int[][] sr = SR(ns);

        //第一轮列混淆
        int[][] mc = MC(sr);

        //第二次轮密钥加
        ak = Ak(mc,Key2);

        //第二轮半字节代替
        ns = NS(ak);

        //第二轮行位移
        sr = SR(ns);

        //第三次轮密钥加
        ak = Ak(sr,Key3);

        ciphertext = InverseHalfBytes(ak);

        return ciphertext;
    }

    //半字节表示转换为数组
    public static int[] InverseHalfBytes(int[][] S) {
        int[] text = new int[16];

        text[0] = (S[0][0] >> 3) & 1;
        text[1] = (S[0][0] >> 2) & 1;
        text[2] = (S[0][0] >> 1) & 1;
        text[3] = S[0][0] & 1;

        text[4] = (S[1][0] >> 3) & 1;
        text[5] = (S[1][0] >> 2) & 1;
        text[6] = (S[1][0] >> 1) & 1;
        text[7] = S[1][0] & 1;

        text[8] = (S[0][1] >> 3) & 1;
        text[9] = (S[0][1] >> 2) & 1;
        text[10] = (S[0][1] >> 1) & 1;
        text[11] = S[0][1] & 1;



        text[12] = (S[1][1] >> 3) & 1;
        text[13] = (S[1][1] >> 2) & 1;
        text[14] = (S[1][1] >> 1) & 1;
        text[15] = S[1][1] & 1;

        return text;
    }

    // 将二进制字符串转换为ASCII字符
    public static String binaryStringToAscii(int[] binaryArray) {
        StringBuilder asciiString = new StringBuilder();
        for (int i = 0; i < binaryArray.length; i += 16) {
            StringBuilder binaryChar = new StringBuilder();
            for (int j = i; j < i + 16; j++) {
                binaryChar.append(binaryArray[j]);
            }
            int asciiValue = Integer.parseInt(binaryChar.toString(), 2);
            asciiString.append((char) asciiValue);
        }
        return asciiString.toString();
    }


    // 将ASCII字符转换为二进制字符串
    public static String asciiToBinaryString(String input) {
        StringBuilder binaryString = new StringBuilder();
        for (char c : input.toCharArray()) {
            String binaryChar = Integer.toBinaryString(c);
            // 确保每个字符都是8位二进制字符串
            while (binaryChar.length() < 16) {
                binaryChar = "0" + binaryChar;
            }
            binaryString.append(binaryChar);
        }
        return binaryString.toString();
    }


// 检查输入是否为有效的二进制字符串
    static boolean isValidBinary(String input) {
        for (char c : input.toCharArray()) {

            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }
}
```
解密代码：
```Java
import java.util.Arrays;

public class Decrypt {
    private static int[] w2;
    private static int[] w3;
    private static int[] w4;
    private static int[] w5;
    static int[][] SBOX = {
            {9,4,10,11},
            {13,1,8,5},
            {6,2,0,3},
            {12,14,15,7}
    };
    //逆S盒
    static int[][] ISBOX = {
            {10,5,9,11},
            {1,7,8,15},
            {6,0,2,3},
            {12,4,13,14}
    };
    static int[][] mix = {
            {9,2},
            {2,9}
    };

    //异或函数
    public static int[] exclusiveOR(int[] array1, int[] array2){

        int[] result = new int[array1.length];
        for (int i = 0; i < array1.length; i++){
            result[i] = array1[i] ^ array2[i];

        }
        return result;
    }
    //SBox查表
    private static int checkSBox(int[] text){
        int row = text[0]*2 + text[1];
        int col = text[2]*2 + text[3];
        int num = SBOX[row][col];
        return num;
    }
    //ISBox查表
    private static int checkISBox(int[] text){
        int row = text[0]*2 + text[1];
        int col = text[2]*2 + text[3];
        int num = ISBOX[row][col];
        return num;
    }

    //整数转换为二进制
    private static int[] IntToBinary(int num){
        int[] temp = new int[4];
        for (int k = 3; k >= 0; k--){
            temp[k] = num & 1;
            num >>= 1;
        }
        return temp;
    }
    //半字节表示
    public static int[][] HalfBytes(int[] text) {
        int[][] S = new int[2][2];

        S[0][0] = (text[0] << 3) | (text[1] << 2) | (text[2] << 1) | text[3];
        S[1][0] = (text[4] << 3) | (text[5] << 2) | (text[6] << 1) | text[7];
        S[0][1] = (text[8] << 3) | (text[9] << 2) | (text[10] << 1) | text[11];
        S[1][1] = (text[12] << 3) | (text[13] << 2) | (text[14] << 1) | text[15];

        return S;
    }


    //交换函数
    public static int[] swapAndMerge(int[] array1, int[] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("数组长度不同.");
        }

        int[] mergedArray = new int[array1.length * 2]; // 创建一个新的数组，长度为两个输入数组的长度之和

        int[] temp = array1;
        array1 = array2;
        array2 = temp;
        // 复制 array1 和 array2 到 mergedArray
        mergedArray = Merge(array1,array2);
        return mergedArray; // 返回合并后的数组
    }

    //合并函数
    public static int[] Merge(int[] array1, int[] array2){
        int[] mergedArray = new int[array1.length * 2]; // 创建一个新的数组，长度为两个输入数组的长度之和

        // 复制 array1 和 array2 到 mergedArray
        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);
        return mergedArray;

    }
    //密钥扩展：16位分为两个8位字密钥，
    public static int[] SubNib(int[] rotnib) {
        // 在这里执行SubNib操作
        int[] N0 = Arrays.copyOfRange(rotnib,0,4);
        int[] N1 = Arrays.copyOfRange(rotnib,4,8);


        int num0 = checkSBox(N0);
        N0 = IntToBinary(num0);

        int num1 = checkSBox(N1);

        N1 = IntToBinary(num1);

        int[] result = Merge(N0,N1);

        return result;
    }


    public static int[] RotNib(int[] rcon) {
        int[] rotnib;
        // 在这里执行RotNib操作
        int[] N0 = Arrays.copyOfRange(rcon,0,4);
        int[] N1 = Arrays.copyOfRange(rcon,4,8);

        rcon = swapAndMerge(N0,N1);
        return rcon;
    }

    public static int[] Rcon(int round) {
        int[] rcon = null;
        if(round == 1){
            rcon = new int[]{1, 0, 0, 0, 0, 0, 0, 0};
        }
        else if (round == 2){
            rcon = new int[]{0,0,1,1,0,0,0,0};
        }
        return rcon;
    }
    public static int[] g(int[] w,int i){
        int[] rotnib = RotNib(w);
        int[] subnib = SubNib(rotnib);
        int[] rcon = Rcon(i);
        int[] reslut = exclusiveOR(rcon,subnib);
        return reslut;
    }
    public static void KeyExpansion(int[] key){

        int[] w0 = Arrays.copyOfRange(key,0,8);
        int[] w1 = Arrays.copyOfRange(key,8,16);

        int[] g1 = g(w1,1);

        w2 = exclusiveOR(w0,g1);
        w3 = exclusiveOR(w2,w1);

        int[] g2 = g(w3,2);
        w4 = exclusiveOR(w2,g2);
        w5 = exclusiveOR(w4,w3);

    }

    //密钥加函数Ak（逆函数是本身）
    private static int[][] Ak(int[][] plaintext, int[][] key){
        int[][] reslut = new int[2][2];

        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 2; j++){
                reslut[i][j] =plaintext[i][j]^key[i][j];
            }
        }
        return reslut;
    }

    //逆半字节代替INS
    private static int[][] INS(int[][] ak){
        int[][] reslut = new int[2][2];
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 2; j++){
                int[] temp = IntToBinary(ak[i][j]);
                reslut[i][j] = checkISBox(temp);
            }
        }
        return reslut;
    }
    //逆行移位ISR（同SR）
    private static int[][] ISR(int[][] halfBytes){
        int[][] result = halfBytes;
        int temp = halfBytes[1][0];
        halfBytes[1][0] = halfBytes[1][1];
        halfBytes[1][1] = temp;
        return result;
    }

    //逆列混淆IMC
    private static int[][] IMC(int[][] sr){
        int[][] result = new int[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j] = multiplyGF4(mix[i][0], sr[0][j]) ^ multiplyGF4(mix[i][1], sr[1][j]);
            }
        }

        return result;
    }
    public static int multiplyGF4(int a, int b) {
        int product = 0;

        while (a > 0) {
            if ((a & 1) == 1) {
                product ^= b;
            }

            boolean carry = (b & 8) != 0;
            b = (b << 1) & 15;
            if (carry) {
                b ^= 3;
            }

            a = a >> 1;
        }

        return product;
    }

    public static int[] decrypt(int[] ciphertext, int[] key) {
        int[] plaintext;
        int[][] Ciphertext = HalfBytes(ciphertext);

        //扩展密钥
        KeyExpansion(key);
        int[][] Key1 = HalfBytes(key);
        int[][] Key2 =HalfBytes( Merge(w2,w3));
        int[][] Key3 = HalfBytes(Merge(w4,w5));

        //第一次轮密钥加
        int[][] ak = Ak(Ciphertext,Key3);

        //第一轮行位移
        int[][] isr = ISR(ak);

        //第一轮半字节代替
        int[][] ins = INS(isr);

        //第二次轮密钥加
        ak = Ak(ins,Key2);

        //第一轮列混淆
        int[][] imc = IMC(ak);

        //第二轮行位移
        isr = ISR(imc);

        //第二轮半字节代替
        ins = INS(isr);

        //第三次轮密钥加
        ak = Ak(ins,Key1);

        plaintext = InverseHalfBytes(ak);
        return plaintext;
    }

    //半字节表示转换为数组
    public static int[] InverseHalfBytes(int[][] S) {
        int[] text = new int[16];

        text[0] = (S[0][0] >> 3) & 1;
        text[1] = (S[0][0] >> 2) & 1;
        text[2] = (S[0][0] >> 1) & 1;
        text[3] = S[0][0] & 1;

        text[4] = (S[1][0] >> 3) & 1;
        text[5] = (S[1][0] >> 2) & 1;
        text[6] = (S[1][0] >> 1) & 1;
        text[7] = S[1][0] & 1;

        text[8] = (S[0][1] >> 3) & 1;
        text[9] = (S[0][1] >> 2) & 1;
        text[10] = (S[0][1] >> 1) & 1;
        text[11] = S[0][1] & 1;



        text[12] = (S[1][1] >> 3) & 1;
        text[13] = (S[1][1] >> 2) & 1;
        text[14] = (S[1][1] >> 1) & 1;
        text[15] = S[1][1] & 1;

        return text;
    }

    // 将二进制字符串转换为ASCII字符
    public static String binaryStringToAscii(int[] binaryArray) {
        StringBuilder asciiString = new StringBuilder();
        for (int i = 0; i < binaryArray.length; i += 16) {
            StringBuilder binaryChar = new StringBuilder();
            for (int j = i; j < i + 16; j++) {
                binaryChar.append(binaryArray[j]);
            }
            int asciiValue = Integer.parseInt(binaryChar.toString(), 2);
            asciiString.append((char) asciiValue);
        }
        return asciiString.toString();
    }


    // 将ASCII字符转换为二进制字符串
    public static String asciiToBinaryString(String input) {
        StringBuilder binaryString = new StringBuilder();
        for (char c : input.toCharArray()) {
            String binaryChar = Integer.toBinaryString(c);
            // 确保每个字符都是8位二进制字符串
            while (binaryChar.length() < 16) {
                binaryChar = "0" + binaryChar;
            }
            binaryString.append(binaryChar);
        }
        return binaryString.toString();
    }
    static boolean isValidBinary(String input) {
        for (char c : input.toCharArray()) {

            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }
}
```
运行结果：
首页界面：
<img width="291" alt="index" src="https://github.com/Yuzi1/S_AES/assets/94826086/14c12698-1bf5-42a9-825a-8fc462ff2c92">
加密：
<img width="291" alt="加密" src="https://github.com/Yuzi1/S_AES/assets/94826086/375b7b4c-67b1-493e-9124-ad23d81ae475">
解密：
<img width="291" alt="解密" src="https://github.com/Yuzi1/S_AES/assets/94826086/f29490d4-fef2-4588-bbda-626c49e8deb3">

2.2 第2关：交叉测试考虑到是"算法标准"，所有人在编写程序的时候需要使用相同算法流程和转换单元(替换盒、列混淆矩阵等)，以保证算法和程序在异构的系统或平台上都可以正常运行。设有A和B两组位同学(选择相同的密钥K)；则A、B组同学编写的程序对明文P进行加密得到相同的密文C；或者B组同学接收到A组程序加密的密文C，使用B组程序进行解密可得到与A相同的P。
交互测试：
![otherReslut](https://github.com/Yuzi1/S_AES/assets/94826086/9e3bf3b9-9641-4571-b7ce-79c03060b9f7)

<img width="291" alt="myReslut" src="https://github.com/Yuzi1/S_AES/assets/94826086/62a50859-9d6f-4306-909a-bd7b1174bee7">

2.3 第3关：扩展功能考虑到向实用性扩展，加密算法的数据输入可以是ASII编码字符串(分组为2 Bytes)，对应地输出也可以是ACII字符串(很可能是乱码)。
代码：
```Java
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
                    if (plaintext.length() != 16 || key.length() != 16 || !Encrypt.isValidBinary(plaintext) || !Encrypt.isValidBinary(key)) {
                        result = "请输入有效位数的明文和密钥。";
                    }
                    else {
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
                }
                else if ("ASCII编码".equals(type)){
                    // 将ASCII编码转换为二进制
                    String binaryPlaintext = Encrypt.asciiToBinaryString(plaintext);
                    System.out.println(binaryPlaintext);

                    System.out.println(binaryPlaintext.length());
                    System.out.println(key.length());
                    //System.out.println(key.length());
                    //System.out.println(key.length());
                    if (binaryPlaintext.length() != 16*16 || key.length() != 16 || !Encrypt.isValidBinary(binaryPlaintext) || !Encrypt.isValidBinary(key)){
                        result = "请输入有效位数的明文和密钥。";
                    }
                    else{
                        // 将明文和密钥转换为整数数组
                        int[] plaintextBits = new int[16*16];
                        int[] keyBits = new int[16];


                        for (int i = 0; i < 16*16; i++) {
                            plaintextBits[i] = Integer.parseInt(Character.toString(binaryPlaintext.charAt(i)));
                        }
                        for (int i = 0; i < 16; i++) {
                            keyBits[i] = Integer.parseInt(Character.toString(key.charAt(i)));
                        }

                        int[] ciphertextBits = new int[16 * 16];


                        // 使用S-AES的加密方法
                        for (int i = 0; i < 16; i++) {
                            // 提取当前16位明文
                            int[] currentPlaintext = Arrays.copyOfRange(plaintextBits, i * 16, (i + 1) * 16);

                            // 执行S-AES加密操作，将加密结果存储在encryptedBits中的相应位置
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, keyBits);

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
```
加密和解密算法不变。
运行结果：
<img width="291" alt="String" src="https://github.com/Yuzi1/S_AES/assets/94826086/cd761f69-2c5b-4236-b9e5-6f921a837a9d">

2.4 第4关：多重加密
2.4.1 双重加密将S-AES算法通过双重加密进行扩展，分组长度仍然是16 bits，但密钥长度为32 bits。
加密部分核心代码：
```Java
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
                        // 使用S-DES的加密方法
                        int[] midtext = Encrypt.encrypt(plaintextBits, key1);
                        int[] ciphertextBits = Encrypt.encrypt(midtext,key2);

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
                            // 提取当前16位明文
                            int[] currentPlaintext = ciphertextBits;

                            // 执行S-AES加密操作，将加密结果存储在encryptedBits中的相应位置
                            int[] currentEncrypted = Encrypt.encrypt(currentPlaintext, key2);

                            // 将当前加密结果存储在整体加密结果数组中的相应位置
                            System.arraycopy(currentEncrypted, 0, ciphertextBits, i * 16, 16);
                        }
                        // 打印整体加密结果
                        result = "加密结果: "+ Encrypt.binaryStringToAscii(ciphertextBits);
                    }

                }

                resultArea.setText(result);
            }
```
解密部分核心代码：  
```Java
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
                        // 解密
                        int[] midtext = Decrypt.decrypt(ciphertextBits, key2);
                        System.out.println(Arrays.toString(midtext));
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


                    // 使用S-AES的解密方法
                    for (int i = 0; i < 16; i++) {
                        // 提取当前16位密文
                        int[] currentCiphertext = Arrays.copyOfRange(ciphertextBits, i * 16, (i + 1) * 16);

                        // 执行S-AES解密操作，将加密结果存储在encryptedBits中的相应位置
                        int[] currentDecrypted = Decrypt.decrypt(currentCiphertext, key2);

                        // 将当前加密结果存储在整体加密结果数组中的相应位置
                        System.arraycopy(currentDecrypted, 0, plaintextBits, i * 16, 16);
                    }
                    for (int i = 0; i < 16; i++) {
                        // 提取当前16位密文
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
```
运行结果：
<img width="291" alt="双重加密" src="https://github.com/Yuzi1/S_AES/assets/94826086/4b605266-841a-4519-97c0-f9cd3467e104">
<img width="291" alt="双重解密" src="https://github.com/Yuzi1/S_AES/assets/94826086/ecb42761-8dde-4df6-8210-529bc2601e88">

2.4.2 中间相遇攻击假设你找到了使用相同密钥的明、密文对(一个或多个)，请尝试使用中间相遇攻击的方法找到正确的密钥Key(K1+K2)。
代码如下：
```Java
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class MidAttack {
    private static final AtomicBoolean keyFound = new AtomicBoolean(false);

    public static void main(String[] args) {
        //明密文对1
        int[] plaintext1 = {0,1,1,0,1,1,1,1,0,1,1,0,1,0,1,1};
        int[] ciphertext1 = {0,0,0,1,1,0,0,1,0,1,1,1,0,1,1,1};

        //明密文对2
        int[] plaintext2 = {0,0,1,1,1,1,1,0,0,0,0,0,1,1,1,1};
        int[] ciphertext2 = {0,0,0,0,1,1,0,1,1,1,0,0,0,0,1,1};

        // 创建两个线程用于密钥搜索，缩小每个线程密钥搜索的范围
        Thread thread1 = new Thread(new KeySearch(0, 2147483647, plaintext1, ciphertext1,plaintext2, ciphertext2));
        Thread thread2 = new Thread(new KeySearch(2147483648L, 4294967295L, plaintext1, ciphertext1,plaintext2, ciphertext2));

        // 启动线程
        thread1.start();
        thread2.start();

        try {
            // 等待线程结束
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class KeySearch implements Runnable {
        private final long start;
        private final long end;
        private final int[] plaintext1,plaintext2;
        private final int[] ciphertext1,ciphertext2;

        KeySearch(long start, long end, int[] plaintext1, int[] ciphertext1,int[] plaintext2, int[] ciphertext2) {
            this.start = start;
            this.end = end;
            this.plaintext1 = plaintext1;
            this.ciphertext1 = ciphertext1;
            this.plaintext2 = plaintext2;
            this.ciphertext2 = ciphertext2;
        }

        @Override
        public void run() {
            for (long i = start; i <= end; i++) {
                if (keyFound.get()) {
                    break;
                }

                // 将长整型密钥转换为二进制数组
                int[] keyBits = longToBinaryArray(i);
                // 拆分密钥为两部分
                int[] key1 = Arrays.copyOfRange(keyBits, 0, 16);
                int[] key2 = Arrays.copyOfRange(keyBits, 16, 32);
                // 使用密钥尝试解密和加密
                int[] decryptedText1 = Decrypt.decrypt(ciphertext1, key2);
                int[] encryptedText1 = Encrypt.encrypt(plaintext1, key1);

                int[] decryptedText2 = Decrypt.decrypt(ciphertext2, key2);
                int[] encryptedText2 = Encrypt.encrypt(plaintext2, key1);

                //保证密钥唯一
                if (Arrays.equals(decryptedText1, encryptedText1) && Arrays.equals(decryptedText2, encryptedText2)) {
                    String result = "密钥: ";
                    for (int bit : keyBits) {
                        result += bit;
                    }
                    System.out.println(result);

                    keyFound.set(true);
                    break;
                }
            }
        }
    }

    // 将长整型转换为32位的二进制数组
    public static int[] longToBinaryArray(long num) {
        String binaryString = Long.toBinaryString(num);

        while (binaryString.length() < 32) {
            binaryString = "0" + binaryString;
        }

        int[] binaryArray = new int[32];
        for (int i = 0; i < 32; i++) {
            binaryArray[i] = Character.getNumericValue(binaryString.charAt(i));
        }

        return binaryArray;
    }
}
```
运行结果：
<img width="640" alt="MidAttack" src="https://github.com/Yuzi1/S_AES/assets/94826086/3c5aac9b-e19f-43d5-a1a7-144802ee7f8d">

2.4.3 三重加密将S-AES算法通过三重加密进行扩展，按照32 bits密钥Key(K1+K2)的模式进行三重加密解密。
加密核心代码：
```Java
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
```
解密核心代码：
```Java
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
```
运行结果：
加密：
<img width="291" alt="三重加密" src="https://github.com/Yuzi1/S_AES/assets/94826086/695bbc6f-2958-4b15-880e-ae41742469c4">
解密：
<img width="291" alt="三重解密" src="https://github.com/Yuzi1/S_AES/assets/94826086/bb5710a2-26b6-440f-a329-d68507df0ac1">

2.5 第5关：工作模式基于S-AES算法，使用密码分组链(CBC)模式对较长的明文消息进行加密。注意初始向量(16 bits) 的生成，并需要加解密双方共享。在CBC模式下进行加密，并尝试对密文分组进行替换或修改，然后进行解密，请对比篡改密文前后的解密结果。
代码如下：
```Java
import java.util.Arrays;

public class CBC {
    static int[] plaintext = {0,0,1,0,1,1,0,1,0,1,0,1,0,1,0,1,0,1,1,0,0,1,1,1,0,0,1,1,1,0,1,1,0,0,1,1,1,1,1,0,0,1,0,0,1,1};
    static int[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] key = {1,0,0,1,0,0,1,0,0,1,0,1,0,0,1,0,0,1,1,0,1,0,1,1,1,1,0,0,1,0,0,1};

    static int plenght;
    int[] ciphertext = encryptCBC(plaintext, iv);
    static int[] key1 = Arrays.copyOfRange(key,0,16);
    static int[] key2 = Arrays.copyOfRange(key,16,32);


    public static void main(String[] args) {
        plenght = plaintext.length;
        System.out.println("明文: " + Arrays.toString(plaintext));


        //对明文加密
        int[] ciphertext = encryptCBC(plaintext, iv);
        System.out.println("原始密文: " + Arrays.toString(ciphertext));

        //对上面加密的密文解密
        int[] DePlaintext = decryptCBC(ciphertext,iv,plenght);
        System.out.println("原始的明文: " + Arrays.toString(plaintext));
        System.out.println("解密的明文: " + Arrays.toString(DePlaintext));

        // 尝试修改密文块
        ciphertext[16] = 0;
        ciphertext[13] = 1;

        ciphertext[23] = 0;

        // 对篡改后的密文解密
        int[] DePlaintext1 = decryptCBC(ciphertext, iv, plenght);
        System.out.println("篡改的明文: " + Arrays.toString(DePlaintext1));
    }

    public static int[] encryptCBC(int[] plaintext, int[] iv) {
        if (plaintext.length % 16 != 0) {
            int paddingLength = 16 - (plaintext.length % 16);
            int[] paddedPlaintext = new int[plaintext.length + paddingLength];
            System.arraycopy(plaintext, 0, paddedPlaintext, 0, plaintext.length);
            // 后面添加0，直到长度为16的倍数
            for (int i = plaintext.length; i < paddedPlaintext.length; i++) {
                paddedPlaintext[i] = 0;
            }
            plaintext = paddedPlaintext;
        }

        System.out.println(Arrays.toString(plaintext));
        int[] ciphertext = new int[plaintext.length];
        int[] previousCipherBlock = iv;

        for (int i = 0; i < plaintext.length; i += 16) {
            int[] dataBlock = Arrays.copyOfRange(plaintext, i, i + 16);
            dataBlock = Encrypt.exclusiveOR(dataBlock,previousCipherBlock);

            int[] encryptedBlock = Encrypt.encrypt(dataBlock, key1);
            encryptedBlock =Encrypt.encrypt(encryptedBlock,key2);
            encryptedBlock = Encrypt.encrypt(encryptedBlock,key1);


            System.arraycopy(encryptedBlock, 0, ciphertext, i, 16);
            previousCipherBlock = encryptedBlock;
            System.out.println(Arrays.toString(previousCipherBlock));
        }

        return ciphertext;
    }

    public static int[] decryptCBC(int[] ciphertext, int[] iv, int originalLength) {
        int[] plaintext = new int[ciphertext.length];
        int[] previousCipherBlock = iv;

        for (int i = 0; i < ciphertext.length; i += 16) {
            int[] encryptedBlock = Arrays.copyOfRange(ciphertext, i, i + 16);
            int[] decryptedBlock = Decrypt.decrypt(encryptedBlock, key1);
            decryptedBlock = Decrypt.decrypt(decryptedBlock, key2);
            decryptedBlock = Decrypt.decrypt(decryptedBlock, key1);

            // 解密后与前一个块异或以获取原始数据
            int[] originalDataBlock = Decrypt.exclusiveOR(decryptedBlock, previousCipherBlock);

            System.arraycopy(originalDataBlock, 0, plaintext, i, 16);
            previousCipherBlock = encryptedBlock;
        }

        // 移除填充数据
        int originalEnd =originalLength;
        return Arrays.copyOfRange(plaintext, 0, originalEnd);
    }

}
```
运行结果：
<img width="1280" alt="CBC" src="https://github.com/Yuzi1/S_AES/assets/94826086/b9d9f7f7-c116-41e1-9737-62ff4c5fe068">
