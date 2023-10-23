import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        int [] plaintext = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        int [] key = new int[]{0,0,1,0,1,1,0,1,0,1,0,1,0,1,0,1};
        plaintext = new int[]{0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1};
        plaintext = new int[]{1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0};

        S_AES s_aes = new  S_AES();
        System.out.println("plaintext: " + Arrays.toString(plaintext));
        int [] ciphertext = s_aes.encrypt(plaintext, key);
        System.out.println("ciphertext: " + Arrays.toString(ciphertext));
        System.out.println("plaintext: " + Arrays.toString(s_aes.decrypt(ciphertext, key)));
    }
}
