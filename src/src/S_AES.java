package src;
import java.util.ArrayList;

public class S_AES {

    //s盒与逆s盒
    int[][] s_box = {{9, 4, 10, 11}, {13, 1, 8, 5}, {6, 2, 0, 3}, {12, 14, 15, 7}};
    int[][] Is_box = {{10, 5, 9, 11}, {1, 7, 8, 15}, {6, 0, 2, 3}, {12, 4, 13, 14}};

    int [] ignoredRCON1 = {1,0,0,0,0,0,0,0};
    int [] ignoredRCON2 = {0,0,1,1,0,0,0,0};

    int [] MC_table = {1,4,4,1};
    int [] IMC_table = {9,2,2,9};

    //16位转化为2*2的矩阵，用一维数组代替
    public int[] key_to_matrix(int[] key_combine) {
        int [] res = new int[4];
        res[0] = key_combine[0]*8 + key_combine[1]*4+ key_combine[2]*2 + key_combine[3];
        res[2] = key_combine[4]*8 + key_combine[5]*4+ key_combine[6]*2 + key_combine[7];
        res[1] = key_combine[8]*8 + key_combine[9]*4+ key_combine[10]*2 + key_combine[11];
        res[3] = key_combine[12]*8 + key_combine[13]*4+ key_combine[14]*2 + key_combine[15];

        return res;
    }


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

    //将四位矩阵转化为16位二进制数
    public int[] matrix_to_binary(int[] matrix) {
        int [] binaryArray = new int[16];
        int [] temp1 = to_binary(matrix[0]);
        int [] temp2 = to_binary(matrix[1]);
        int [] temp3 = to_binary(matrix[2]);
        int [] temp4 = to_binary(matrix[3]);
        System.arraycopy(temp1, 0, binaryArray, 0, 4);
        System.arraycopy(temp3, 0, binaryArray, 4, 4);
        System.arraycopy(temp2, 0, binaryArray, 8, 4);
        System.arraycopy(temp4, 0, binaryArray, 12, 4);
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

    //轮密钥加
    public int [] add_round_key(int [] plaintext, int [] key1, int [] key2){
        int [] key_combine = new int[16];
        System.arraycopy(key1, 0, key_combine, 0, 8);
        System.arraycopy(key2, 0, key_combine, 8, 8);

        if (plaintext.length==16) return bitwiseXOR(plaintext, key_combine);
        else return bitwiseXOR(plaintext, key_to_matrix(key_combine));
    }


    //半字节替代和逆半字节替代，根据输入的box确定
    public int[] substitute_bytes(int[] input, int[][] s_box) {
        int [] res = new int[4];
        for (int i = 0; i < 4; i++) {
            res[i] =  sub_bytes(input[i], s_box);
        }
        return  res;
    }
    public int sub_bytes(int k,int [][] s_box) {
        int [] input = to_binary(k);
        //输入的4位二进制数，两位为行两位为列
        int x = input[0]*2+input[1];
        int y = input[2]*2+input[3];

        return s_box[x][y];
    }

    //拓展密钥，16位的初始密钥被分为w0，w1两个8位字
    public ArrayList<int[]> expand_key(int[] key) {
        //初始密钥w0,w1分别是key的前8位和后8位
        ArrayList<int []> res = new ArrayList<>();
        int[] w0 = new int[8];
        int[] w1 = new int[8];
        System.arraycopy(key, 0, w0, 0, 8);
        System.arraycopy(key, 8, w1, 0, 8);
        int [] w2 = bitwiseXOR(w0,fun_g(w1,ignoredRCON1));
        int [] w3 = bitwiseXOR(w2,w1);
        int [] w4 = bitwiseXOR(w2,fun_g(w3,ignoredRCON2));
        int [] w5 = bitwiseXOR(w4,w3);
        res.add(w0);res.add(w1);res.add(w2);res.add(w3);res.add(w4);res.add(w5);

        return res;
    }

    //g函数是在拓展密钥中使用的函数
    int[] fun_g(int[] w,int [] ignoredRCON) {
        int[] N1 = new int[4];
        int[] N2 = new int[4];
        System.arraycopy(w, 0, N1, 0, 4);
        System.arraycopy(w, 4, N2, 0, 4);
        int k1 = N1[0]*8 + N1[1]*4 + N1[2]*2 + N1[3];
        int k2 = N2[0]*8 + N2[1]*4 + N2[2]*2 + N2[3];
        N1 = to_binary(sub_bytes(k1,s_box));
        N2 = to_binary(sub_bytes(k2,s_box));
        int [] combine_N12 = new int[8];
        System.arraycopy(N2, 0, combine_N12, 0, 4);
        System.arraycopy(N1, 0, combine_N12, 4, 4);

        return bitwiseXOR(combine_N12,ignoredRCON);
    }

    //行位移，第一行不变，第二行移动一位
    int []  row_shift(int [] row) {
        int temp = row[2];
        row[2] = row[3];
        row[3] = temp;
        return row;
    }

    //列混淆和逆列混淆，定义为一个函数，可以设置参数来区分是列混淆还是逆列混淆,GF(2^4)上的算术已经在对应的类中定义
    public int [] MC(int [] temp, int [] key){
        //输入temp是列混淆或者逆列混淆的参数矩阵，key是已经处理过的密钥（非初始密钥）
        int s0_0 = GF2_4.add(GF2_4.multiply(temp[0],key[0]),GF2_4.multiply(temp[1],key[2]));
        int s0_1 = GF2_4.add(GF2_4.multiply(temp[0],key[1]),GF2_4.multiply(temp[1],key[3]));
        int s1_0 = GF2_4.add(GF2_4.multiply(temp[2],key[0]),GF2_4.multiply(temp[3],key[2]));
        int s1_1 = GF2_4.add(GF2_4.multiply(temp[2],key[1]),GF2_4.multiply(temp[3],key[3]));

        return new int [] {s0_0,s0_1,s1_0,s1_1};
    }

    public int [] encrypt(int [] plaintext, int [] key){
        //先拓展密钥被哟
        ArrayList<int []> expand_keys = expand_key(key);
        //转化为状态矩阵
        plaintext = key_to_matrix(plaintext);
        //第一步，轮密钥加
        plaintext = add_round_key(plaintext, expand_keys.get(0), expand_keys.get(1));
        //第二步，半字节替代
        plaintext = substitute_bytes(plaintext,s_box);
        //第三步，行位移
        plaintext = row_shift(plaintext);
        //第四步，列混淆
        plaintext = MC(MC_table,plaintext);
        //第五步，论密钥加
        plaintext = add_round_key(plaintext, expand_keys.get(2), expand_keys.get(3));
        //第五步，半字节替代
        plaintext = substitute_bytes(plaintext,s_box);
        //第六步，行位移
        plaintext = row_shift(plaintext);
        //第七步，轮密钥加
        plaintext = add_round_key(plaintext, expand_keys.get(4), expand_keys.get(5));
        return matrix_to_binary(plaintext);
    }

    public int [] decrypt(int [] ciphertext, int [] key){
        ArrayList<int []> expand_keys = expand_key(key);
        //转化为状态矩阵
        ciphertext = key_to_matrix(ciphertext);
        //论密钥加
        ciphertext = add_round_key(ciphertext, expand_keys.get(4), expand_keys.get(5));
        //逆行位移
        ciphertext = row_shift(ciphertext);
        //逆半字节替代
        ciphertext = substitute_bytes(ciphertext,Is_box);
        //论密钥加
        ciphertext = add_round_key(ciphertext, expand_keys.get(2), expand_keys.get(3));
        //逆列混淆
        ciphertext = MC(IMC_table,ciphertext);
        //逆行移位
        ciphertext = row_shift(ciphertext);
        //逆半字节替代
        ciphertext = substitute_bytes(ciphertext,Is_box);
        //论密钥加
        ciphertext = add_round_key(ciphertext, expand_keys.get(0), expand_keys.get(1));

        return matrix_to_binary(ciphertext);
    }

    //输入为字符串时的加密
    public String encrypt(String plaintext, String keys){
        StringBinaryTransfer sb = new StringBinaryTransfer();
        int [] key = sb.intStringToBinary(keys);
                //先将字符串转化为16位二进制数的列表
        ArrayList<int []> plaintext_list = sb.SBTransfer(plaintext);
        ArrayList<int []> ciphertext_list = new ArrayList<>();
        for (int [] p: plaintext_list){
            int [] temp = encrypt(p,key);
            ciphertext_list.add(temp);
        }

        return sb.BSTransfer(ciphertext_list);
    }

    //输入为字符串时的解密
    public String decrypt(String ciphertext,String keys){
        StringBinaryTransfer sb = new StringBinaryTransfer();
        int [] key = sb.intStringToBinary(keys);
        ArrayList<int []> ciphertext_list = sb.SBTransfer(ciphertext);
        ArrayList<int []> plaintext_list = new ArrayList<>();
        for(int [] c: ciphertext_list){
            int [] temp = decrypt(c,key);
            plaintext_list.add(temp);
        }
        return sb.BSTransfer(plaintext_list);
    }

    //双重加密，key为32位密钥，key = key1+key2，先用key1加密，再使用key2加密
    public int [] double_encrypt(int [] plaintext, int [] key){
        int [] key1 = new int[16];
        int [] key2 = new int[16];
        System.arraycopy(key,0,key1,0,16);
        System.arraycopy(key,16,key2,0,16);
        //第一轮加密
        int [] temp_ciphertext = encrypt(plaintext,key1);
        //第二轮加密
        return encrypt(temp_ciphertext,key2);
    }

    public int [] double_decrypt(int [] ciphertext, int [] key){
        int [] key1 = new int[16];
        int [] key2 = new int[16];
        System.arraycopy(key,0,key1,0,16);
        System.arraycopy(key,16,key2,0,16);
        int [] temp_plaintext = decrypt(ciphertext,key2);
        return  decrypt(temp_plaintext,key1);
    }

    //三重加密，key变成48位分组密钥，key = key1+key2+key3，先用key1加密，再使用key2加密，最后使用key3加密
    public int [] triple_encrypt(int [] plaintext, int [] key){
        int [] key1 = new int[16];
        int [] key2 = new int[16];
        int [] key3 = new int[16];
        System.arraycopy(key,0,key1,0,16);
        System.arraycopy(key,16,key2,0,16);
        System.arraycopy(key,32,key3,0,16);
        int [] temp_ciphertext = encrypt(plaintext,key1);
        int [] temp_temp_ciphertext = encrypt(temp_ciphertext,key2);
        return encrypt(temp_temp_ciphertext,key3);
    }

    public int [] triple_decrypt(int [] ciphertext, int [] key){
        int [] key1 = new int[16];
        int [] key2 = new int[16];
        int [] key3 = new int[16];
        System.arraycopy(key,0,key1,0,16);
        System.arraycopy(key,16,key2,0,16);
        System.arraycopy(key,32,key3,0,16);
        int [] temp_plaintext = decrypt(ciphertext,key3);
        int [] temp_temp_plaintext = decrypt(temp_plaintext,key2);
        return decrypt(temp_temp_plaintext,key1);
    }


}
