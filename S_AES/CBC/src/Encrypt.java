import java.util.Arrays;

public class Encrypt {
    private static int[] w2;
    private static int[] w3;
    private static int[] w4;
    private static int[] w5;

    //S�У�4x4���ֽ�ֵ���󣩣���������4λֵ������
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
    //�����
    public static int[] exclusiveOR(int[] array1, int[] array2){

        int[] result = new int[array1.length];
        for (int i = 0; i < array1.length; i++){
            result[i] = array1[i] ^ array2[i];

        }
        return result;
    }

    //SBox���
    private static int checkSBox(int[] text){
        int row = text[0]*2 + text[1];
        int col = text[2]*2 + text[3];
        int num = SBOX[row][col];
        return num;
    }

    //����ת��Ϊ������
    public static int[] IntToBinary(int num){
        int[] temp = new int[4];
        for (int k = 3; k >= 0; k--){
            temp[k] = num & 1;
            num >>= 1;
        }
        return temp;
    }
    //���ֽڱ�ʾ
    public static int[][] HalfBytes(int[] text) {
        int[][] S = new int[2][2];

        S[0][0] = (text[0] << 3) | (text[1] << 2) | (text[2] << 1) | text[3];
        S[0][1] = (text[8] << 3) | (text[9] << 2) | (text[10] << 1) | text[11];
        S[1][0] = (text[4] << 3) | (text[5] << 2) | (text[6] << 1) | text[7];
        S[1][1] = (text[12] << 3) | (text[13] << 2) | (text[14] << 1) | text[15];

        return S;
    }


    //��������
    public static int[] swapAndMerge(int[] array1, int[] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("���鳤�Ȳ�ͬ.");
        }

        int[] mergedArray ; // ����һ���µ����飬����Ϊ������������ĳ���֮��

        int[] temp = array1;
        array1 = array2;
        array2 = temp;
        // ���� array1 �� array2 �� mergedArray
        mergedArray = Merge(array1,array2);
        return mergedArray; // ���غϲ��������
    }

    //�ϲ�����
    public static int[] Merge(int[] array1, int[] array2){
        int[] mergedArray = new int[array1.length * 2]; // ����һ���µ����飬����Ϊ������������ĳ���֮��

        // ���� array1 �� array2 �� mergedArray
        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);
        return mergedArray;

    }
    //��Կ��չ��16λ��Ϊ����8λ����Կ��
    public static int[] SubNib(int[] rotnib) {
        // ������ִ��SubNib����
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
        // ������ִ��RotNib����
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


    //��Կ�Ӻ���Ak���溯���Ǳ�����
    private static int[][] Ak(int[][] plaintext, int[][] key){
        int[][] reslut = new int[2][2];

        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 2; j++){
                reslut[i][j] =plaintext[i][j]^key[i][j];
            }
        }
        return reslut;
    }

    //���ֽڴ���NS
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

    //����λSR
    private static int[][] SR(int[][] halfBytes){
        int[][] result = halfBytes;
        int temp = halfBytes[1][0];
        halfBytes[1][0] = halfBytes[1][1];
        halfBytes[1][1] = temp;
        return result;
    }

    //�л���MC,����˷�
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

    //����
    public static  int[] encrypt(int[] plaintext, int[] key){
        int[] ciphertext;
        int[][] Plaintext = HalfBytes(plaintext);

        //��չ��Կ
        KeyExpansion(key);
        int[][] Key1 = HalfBytes(key);
        int[][] Key2 =HalfBytes( Merge(w2,w3));
        int[][] Key3 = HalfBytes(Merge(w4,w5));

        //��һ������Կ��
        int[][] ak = Ak(Plaintext,Key1);

        //��һ�ְ��ֽڴ���
        int[][] ns = NS(ak);


        //��һ����λ��
        int[][] sr = SR(ns);

        //��һ���л���
        int[][] mc = MC(sr);

        //�ڶ�������Կ��
        ak = Ak(mc,Key2);

        //�ڶ��ְ��ֽڴ���
        ns = NS(ak);

        //�ڶ�����λ��
        sr = SR(ns);

        //����������Կ��
        ak = Ak(sr,Key3);

        ciphertext = InverseHalfBytes(ak);

        return ciphertext;
    }

    //���ֽڱ�ʾת��Ϊ����
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

    // ���������ַ���ת��ΪASCII�ַ�
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


    // ��ASCII�ַ�ת��Ϊ�������ַ���
    public static String asciiToBinaryString(String input) {
        StringBuilder binaryString = new StringBuilder();
        for (char c : input.toCharArray()) {
            String binaryChar = Integer.toBinaryString(c);
            // ȷ��ÿ���ַ�����8λ�������ַ���
            while (binaryChar.length() < 16) {
                binaryChar = "0" + binaryChar;
            }
            binaryString.append(binaryChar);
        }
        return binaryString.toString();
    }


// ��������Ƿ�Ϊ��Ч�Ķ������ַ���
    static boolean isValidBinary(String input) {
        for (char c : input.toCharArray()) {

            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }
}