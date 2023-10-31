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
        System.out.println("n0:"+num0);
        N0 = IntToBinary(num0);

        int num1 = checkSBox(N1);
        System.out.println("n1:"+num1);
        N1 = IntToBinary(num1);

        int[] result = new int[N0.length * 2];
        result = Merge(N0,N1);

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

        System.out.println("w0"+Arrays.toString(w0));
        System.out.println("w1"+Arrays.toString(w1));
        int[] g1 = g(w1,1);
        System.out.println("g1"+Arrays.toString(g1));
        w2 = exclusiveOR(w0,g1);
        w3 = exclusiveOR(w2,w1);

        System.out.println("w2:"+Arrays.toString(w2));
        System.out.println("w3:"+Arrays.toString(w3));
        int[] g2 = g(w3,2);
        w4 = exclusiveOR(w2,g2);
        w5 = exclusiveOR(w4,w3);
        System.out.println("w4:"+Arrays.toString(w4));
        System.out.println("w5:"+Arrays.toString(w5));
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

        //
        int[][] ak = Ak(Ciphertext,Key3);

        //
        int[][] isr = ISR(ak);

        //
        int[][] ins = INS(isr);

        //
        ak = Ak(ins,Key2);

        //
        int[][] imc = IMC(ak);

        //
        isr = ISR(imc);

        //
        ins = INS(isr);

        //
        ak = Ak(ins,Key1);

        plaintext = InverseHalfBytes(ak);
        return plaintext;
    }

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
