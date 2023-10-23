
public class GF2_4 {

    // 定义GF(2^4)上的域特性
    private static final int FIELD_SIZE = 16;
    private static final int MODULUS = 0b10011; // x^4 + x + 1

    // GF(2^4)上的加法操作
    public static int add(int a, int b) {
        return a ^ b;
    }

    // GF(2^4)上的乘法操作
    public static int multiply(int a, int b) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            if ((b & 1) == 1) {
                result ^= a;
            }
            boolean carry = (a & 0b1000) == 0b1000;
            a <<= 1;
            if (carry) {
                a ^= MODULUS;
            }
            b >>= 1;
        }
        return result;
    }

    public static void main(String[] args) {
        int a = 11; // 0111 in binary
        int b = 12; // 0011 in binary

        // GF(2^4)上的加法
        int sum = add(a, b);
        System.out.println("Addition result: " + sum);

        // GF(2^4)上的乘法
        int product = multiply(a, b);
        System.out.println("Multiplication result: " + product);
    }
}
