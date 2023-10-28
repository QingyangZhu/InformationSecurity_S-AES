package extend;
import src.S_AES;

public class CBC_Mode {
    private final S_AES sAES;
    private int[] IV;

    public CBC_Mode() {
        sAES = new S_AES();
    }

    public void setIV(int[] iv) {
        if (iv.length == 16) {
            IV = iv;
        } else {
            throw new IllegalArgumentException("IV长度必须为16位");
        }
    }

    public int[] encryptInCBC(int[] plaintext, int[] key) {
        if (IV == null) {
            throw new IllegalStateException("IV未设置。在加密之前调用setIV()。");
        }

        int[] ciphertext = new int[plaintext.length];
        int[] previousBlock = IV;

        for (int i = 0; i < plaintext.length; i += 16) {
            int[] currentBlock = new int[16];
            System.arraycopy(plaintext, i, currentBlock, 0, 16);

            // 与上一个密文块或IV进行异或操作
            currentBlock = S_AES.bitwiseXOR(currentBlock, previousBlock);

            // 加密当前块
            int[] encryptedBlock = sAES.encrypt(currentBlock, key);

            // 将加密块保存为下一轮的上一个密文块
            previousBlock = encryptedBlock;

            // 将加密块追加到密文中
            System.arraycopy(encryptedBlock, 0, ciphertext, i, 16);
        }

        return ciphertext;
    }

    public int[] decryptInCBC(int[] ciphertext, int[] key) {
        if (IV == null) {
            throw new IllegalStateException("IV未设置。在解密之前调用setIV()。");
        }

        int[] plaintext = new int[ciphertext.length];
        int[] previousBlock = IV;

        for (int i = 0; i < ciphertext.length; i += 16) {
            int[] currentBlock = new int[16];
            System.arraycopy(ciphertext, i, currentBlock, 0, 16);

            // 解密当前块
            int[] decryptedBlock = sAES.decrypt(currentBlock, key);

            // 与上一个密文块或IV进行异或操作
            decryptedBlock = sAES.bitwiseXOR(decryptedBlock, previousBlock);

            // 将解密块保存为下一轮的上一个密文块
            previousBlock = currentBlock;

            // 将解密块追加到明文中
            System.arraycopy(decryptedBlock, 0, plaintext, i, 16);
        }

        return plaintext;
    }
}
