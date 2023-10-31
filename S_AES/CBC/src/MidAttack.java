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
