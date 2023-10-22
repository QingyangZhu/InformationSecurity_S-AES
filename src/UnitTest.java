

import java.util.Arrays;

public class UnitTest {
    int[][] s_box = {{9, 4, 10, 11}, {13, 1, 8, 5}, {6, 2, 0, 3}, {12, 14, 15, 7}};
    int[][] s_box1 = {{10, 5, 9, 11}, {1, 7, 8, 15}, {6, 0, 2, 3}, {12, 4, 13, 14}};

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

    //半字节替代
    public int[] sub_bytes(int[] input) {
        //输入的4位二进制数，两位为行两位为列
        int x = input[0]*2+input[1];
        int y = input[2]*2+input[3];

        return to_binary(s_box[x][y]);
    }

    public static void main(String[] args) {
        UnitTest test = new UnitTest();
        int [] test_ = test.to_binary(5);
        System.out.println(Arrays.toString(test.sub_bytes(test_)));
    }
}
