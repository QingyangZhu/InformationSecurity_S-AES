import java.util.ArrayList;
import java.util.List;

public class S_AES {

    //s盒与逆s盒
    int[][] s_box = {{9, 4, 10, 11}, {13, 1, 8, 5}, {6, 2, 0, 3}, {12, 14, 15, 7}};
    int[][] s_box1 = {{10, 5, 9, 11}, {1, 7, 8, 15}, {6, 0, 2, 3}, {12, 4, 13, 14}};

    int [] ignoredRCON1 = {1,0,0,0,0,0,0,0};
    int [] ignoredRCON2 = {0,0,1,1,0,0,0,0};

    //将十进制数字转化为4位二进制数，高位补零
    public int[] to_binary(int decimalNumber) {
        StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(decimalNumber));
        while (binaryString.length() < 4) {
            binaryString.insert(0, "0");
        }
        int[] binaryArray = new int[4];
        for (int i = 0; i < 4; i++) {
            binaryArray[i] = Character.getNumericValue(binaryString.charAt(i));
        }
        return binaryArray;
    }

    //数组各数位异或运算
    public static int[] bitwiseXOR(int[] array1, int[] array2) {
        if (array1.length != array2.length) {
            throw new IllegalArgumentException("数组长度不同！");
        }

        int[] resultArray = new int[array1.length];

        for (int i = 0; i < array1.length; i++) {
            resultArray[i] = array1[i] ^ array2[i];
        }

        return resultArray;
    }


    //半字节替代
    public int[] sub_bytes(int[] input) {
        //输入的4位二进制数，两位为行两位为列
        int x = input[0]*2+input[1];
        int y = input[2]*2+input[3];

        return to_binary(s_box[x][y]);
    }

    //拓展密钥，16位的初始密钥被分为w0，w1两个8位字
    public List<int[]> expand_key(int[] key) {
        //初始密钥w0,w1分别是key的前8位和后8位
        List<int []> res = new ArrayList<>();
        int[] w0 = new int[8];
        int[] w1 = new int[8];
        System.arraycopy(key, 0, w0, 0, 8);
        System.arraycopy(key, 8, w1, 0, 8);
        int [] w2 = bitwiseXOR(w0,fun_g(w1,ignoredRCON1));
        int [] w3 = bitwiseXOR(w2,w1);
        int [] w4 = bitwiseXOR(w3,fun_g(w3,ignoredRCON2));
        int [] w5 = bitwiseXOR(w4,w3);
        res.add(w0);res.add(w1);res.add(w2);res.add(w3);res.add(w4);res.add(w5);

        return res;
    }

    int[] fun_g(int[] w,int [] ignoredRCON) {
        int[] N1 = new int[4];
        int[] N2 = new int[4];
        System.arraycopy(w, 0, N1, 0, 4);
        System.arraycopy(w, 4, N2, 0, 4);
        N1 = sub_bytes(N1);
        N2 = sub_bytes(N2);
        int [] combine_N12 = new int[8];
        System.arraycopy(N1, 0, combine_N12, 0, 4);
        System.arraycopy(N2, 0, combine_N12, 4, 4);

        return bitwiseXOR(combine_N12,ignoredRCON);
    }
}
