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
