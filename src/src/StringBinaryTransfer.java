package src;

import java.util.ArrayList;

//辅助类，实现将长度为2n字符串抓换为n组16位二进制数
public class StringBinaryTransfer {
    public ArrayList<int []> SBTransfer(String s){
        ArrayList<int []> result = new ArrayList<>();
        if (s.length()%2!= 0) s+=' ';
        for (int i = 0; i < s.length()-1; i+=2) {
            int [] temp = charToBinary(s.charAt(i));
            int [] temp1 = charToBinary(s.charAt(i+1));
            int [] res = new int[16];
            System.arraycopy(temp,0,res,8-temp.length,temp.length);
            System.arraycopy(temp1,0,res,16-temp1.length,temp1.length);
            result.add(res);
        }
        return result;
    }

    public String BSTransfer(ArrayList<int []> ss){
        StringBuilder sb = new StringBuilder();
        for(int [] s:ss){
            int [] temp = new int[8];
            int [] temp1 = new int[8];
            System.arraycopy(s,0,temp,0,8);
            System.arraycopy(s,8,temp1,0,8);
            sb.append(binaryToChar(temp));
            sb.append(binaryToChar(temp1));
        }
        return sb.toString();
    }

    public int [] charToBinary(char ch){
        //将字符转化为8位的整型数组，用ASCII码来实现，位数不足在高位补零
        int ascii = ch;
        int[] result = new int[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = ascii & 1;
            ascii >>= 1;
        }
        return result;
    }
    public char binaryToChar(int[] b){
        int ascii = 0;
        for (int i = 0; i < b.length; i++) {
            ascii <<= 1;
            ascii |= b[i];
        }
        return (char) ascii;
    }

    public int [] intStringToBinary(String str){
        int[] result = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            result[i] = Integer.parseInt(String.valueOf(str.charAt(i)));
        }
        return result;
    }

}
