package extend;

import src.S_AES;

import java.util.*;

public class MiddleMeetAttack {
    int [] knownPlaintext_1 = new int[]{1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0};
    int [] knownCiphertext_1 = new int[]{1, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 0};

    int [] plaintext1 = new int[]{1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0};
    int [] plaintext2 = new int[]{1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0};
    int [] plaintext3 = new int[]{1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 0};
    int [] plaintext4 = new int[]{1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1};
    int [] ciphertext1 = new int[]{0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0};
    int [] ciphertext2 = new int[]{0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1};
    int [] ciphertext3 = new int[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1};
    int [] ciphertext4 = new int[]{0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0};

    ArrayList<int []> plaintext_test = new ArrayList<>();
    ArrayList<int []> ciphertext_test = new ArrayList<>();
    MiddleMeetAttack(){
        plaintext_test.add(plaintext1);plaintext_test.add(plaintext2);plaintext_test.add(plaintext3);plaintext_test.add(plaintext4);
        ciphertext_test.add(ciphertext1);ciphertext_test.add(ciphertext2);ciphertext_test.add(ciphertext3);ciphertext_test.add(ciphertext4);
    }



    //通过已知的明文，用2^16种可能的密钥分别进行加密，存储进哈希表中
    public HashMap<Integer, ArrayList<int []>> getMiddleState(int [] knownPlaintext) {
        HashMap<Integer,ArrayList<int []>> middle_state_map = new HashMap<>();
        for (int i = 0; i < 65536; i++) {
            int [] key_test = toArray(i);
            int [] ciphertext_test = new S_AES().encrypt(knownPlaintext, key_test);
            if(middle_state_map.containsKey(binaryArrayToDecimal(ciphertext_test))){
                middle_state_map.get(binaryArrayToDecimal(ciphertext_test)).add(toArray(i));
            }else {

                middle_state_map.put(binaryArrayToDecimal(ciphertext_test), new ArrayList<>());
                middle_state_map.get(binaryArrayToDecimal(ciphertext_test)).add(toArray(i));
            }

        }
        return middle_state_map;
    }

    //2^16种可能的密钥，一一对应一个中间状态
    public ArrayList<int []> getKeyMiddleState(int [] knownPlaintext){
        ArrayList<int []> middle_state_list = new ArrayList<>();
        for (int i = 0; i < 65536; i++) {
            int [] key_test = toArray(i);
            int [] ciphertext_test = new S_AES().encrypt(knownPlaintext, key_test);
            middle_state_list.add(ciphertext_test);
        }
        return middle_state_list;
    }

    //对密文进行解密，每解密一个，将得到的中间状态去哈希表中查看是否存在
    public Set<int []> getKey(int [] knownCiphertext, int [] knownPlaintext){
        HashMap<Integer,ArrayList<int []>> middle_state_map = getMiddleState(knownPlaintext);
        Set<int []> key_ans = new HashSet<>();
        for (int i = 0; i < 65536; i++) {
            System.out.println("第"+i+"匹配");
            int  [] key_test2 = toArray(i);
            int [] plaintext_test2 = new S_AES().decrypt(knownCiphertext, key_test2);
            if(middle_state_map.containsKey(binaryArrayToDecimal(plaintext_test2))){
                for(int [] key_test1:middle_state_map.get(binaryArrayToDecimal(plaintext_test2))){
                    int [] key_test = new int[32];
                    System.arraycopy(key_test1, 0, key_test, 0, 16);
                    System.arraycopy(key_test2, 0, key_test, 16, 16);
                    key_ans.add(key_test);
                }
            }
            else System.out.println("NO");
        }
        return key_ans;
    }

    //
    public ArrayList<int []> getKeyList(int [] knownCiphertext,int [] knownPlaintext){
        ArrayList<int []> middle_state_list = getKeyMiddleState(knownPlaintext);
        ArrayList<int []> key_ans = new ArrayList<>();
        for (int i = 0; i < 65536; i++) {
            System.out.println("第"+i+"匹配");
            int  [] key_test2 = toArray(i);
            int [] plaintext_test2 = new S_AES().decrypt(knownCiphertext, key_test2);
            for (int j = 0; j < 65536; j++) {
                if (Arrays.equals(middle_state_list.get(j), plaintext_test2)){
                    int [] key_test = new int[32];
                    System.arraycopy(key_test2, 0, key_test, 0, 16);
                    System.arraycopy(toArray(j), 0, key_test, 16, 16);
                    key_ans.add(key_test);
                }
            }
        }
        return key_ans;
    }


    //对多个明密文对进行中间相遇攻击，来获取更精确的密钥
    public ArrayList<int[]> getKey(ArrayList<int []> KnownCiphertext, ArrayList<int []> KnownPlaintext) {
        ArrayList<ArrayList<int []>> keys_list = new ArrayList<>();
        for (int i = 0; i < KnownCiphertext.size(); i++){
            keys_list.add(getKeyList(KnownCiphertext.get(i), KnownPlaintext.get(i)));
        }
        Set<Integer[]> commonArrays = new HashSet<>();
        ArrayList<int[]> result = new ArrayList<>();

        if (keys_list.size() > 1) {
            // 遍历第一个 ArrayList 中的数组元素
            ArrayList<int[]> firstList = keys_list.get(0);
            for (int[] integer : firstList) {
                // 检查这个元素是否在其他 ArrayList 中都存在
                boolean isCommon = true;
                for (int i = 1; i < keys_list.size(); i++) {
                    ArrayList<int[]> otherList = keys_list.get(i);
                    if (!isListContainsArray(otherList, integer)) {
                        isCommon = false;
                        break;
                    }
                }

                // 如果这个元素在所有 ArrayList 中都存在，则添加到结果集合
                if (isCommon) {
                    result.add(integer);
                }
            }
        }
        return result;
    }
    public boolean isListContainsArray(ArrayList<int []> list, int [] array){
        for (int [] ints : list) {
            if (Arrays.equals(ints, array)) {
                return true;
            }
        }
        return false;
    }


    public static int[] toIntArray(String str) {
        char[] chars = str.toCharArray();
        int length = chars.length;
        int[] ints = new int[length];
        for (int i = 0; i < length; i++) {
            ints[i] = chars[i] - '0';
        }
        return ints;
    }
    public static int binaryArrayToDecimal(int[] binaryArray) {
        int decimal = 0;
        for (int i = 0; i < binaryArray.length; i++) {
            decimal += binaryArray[i] * Math.pow(2, binaryArray.length - 1 - i);
        }
        return decimal;
    }
    public static int[] toArray(int i) {
        StringBuilder binaryKey = new StringBuilder(Integer.toBinaryString(i));
        while (binaryKey.length() < 16) {
            // 高位补0，确保密钥长度为16位
            binaryKey.insert(0, "0");
        }
        return toIntArray(String.valueOf(binaryKey));
    }

    //去除数组集合中的重复元素
    public Set<int []> removeSame(Set<int []> set){
        Set<int []> res = new HashSet<>();
        Set<String> stringSet = new HashSet<>();
        for(int [] array:set){
            String arrayAsString = Arrays.toString(array);
            stringSet.add(arrayAsString);
        }
        for (String uniqueString : stringSet) {
            res.add(parseArrayFromString(uniqueString));
        }
        return res;
    }

    // 辅助方法：从字符串解析数组
    public static int[] parseArrayFromString(String arrayString) {
        String[] elements = arrayString
                .replace("[", "")
                .replace("]", "")
                .split(", ");
        int[] array = new int[elements.length];
        for (int i = 0; i < elements.length; i++) {
            array[i] = Integer.parseInt(elements[i]);
        }
        return array;
    }

    public static void main(String[] args) {
        MiddleMeetAttack middleMeetAttack = new MiddleMeetAttack();
        ArrayList<int [] > keys =
                middleMeetAttack.getKey(middleMeetAttack.ciphertext_test, middleMeetAttack.plaintext_test);
        for (int [] key1 : keys){
            System.out.println(Arrays.toString(key1));
        }
        System.out.println(keys.size());

    }
}