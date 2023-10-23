import java.util.Arrays;

public class UnitTest {




    public static void main(String[] args) {
        S_AES s_aes = new S_AES();
        int [] temp = new int[]{1,4,4,1};
        int [] key = new int[]{6,4,12,0};
        System.out.println(Arrays.toString(s_aes.MC(temp, key)));

    }
}
