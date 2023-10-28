package test;

import extend.CBC_Mode;

import java.util.ArrayList;
import java.util.Arrays;

public class UnitTest {


    public static void main(String[] args) {
        CBC_Mode cbcMode = new CBC_Mode();

        // 设置初始化向量（IV） - 这是16位二进制数
        int[] iv = {1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0};
        cbcMode.setIV(iv);

        // 设置密钥 - 这是16位二进制数
        int[] key = {0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0};

        // 明文消息 - 这是32位二进制数
        int[] plaintext = {1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1};

        // 加密明文
        int[] ciphertext = cbcMode.encryptInCBC(plaintext, key);

        // 输出加密后的密文
        System.out.println("加密后的密文: " + Arrays.toString(ciphertext));

        // 解密密文
        int[] decryptedText = cbcMode.decryptInCBC(ciphertext, key);

        // 输出解密后的明文
        System.out.println("解密后的明文: " + Arrays.toString(decryptedText));
    }
}

