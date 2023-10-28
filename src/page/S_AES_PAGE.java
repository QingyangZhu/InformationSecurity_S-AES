package page;

import src.S_AES;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class S_AES_PAGE extends JFrame implements ActionListener {

    JButton decode;
    JTextField tfName, tfKey;
    JCheckBox encryptionToggle;


    S_AES_PAGE() {
        getContentPane().setBackground(Color.ORANGE);
        setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("page/imgs/a.png"));
        JLabel image = new JLabel(i1);
        image.setBounds(0, 0, 900, 800);
        add(image);

        JLabel heading = new JLabel("S-AES");
        heading.setBounds(790, 200, 300, 45);
        heading.setFont(new Font("Mongolian Baiti", Font.BOLD, 48));
        heading.setForeground(Color.BLACK);
        add(heading);

        JLabel name = new JLabel("Enter Your Plain Text");
        name.setBounds(790, 300, 300, 20);
        name.setFont(new Font("Mongolian Baiti", Font.BOLD, 22));
        name.setForeground(Color.BLACK);
        add(name);

        tfName = new JTextField();
        tfName.setBounds(790, 340, 300, 25);
        tfName.setFont(new Font("Times New Roaman", Font.BOLD, 20));
        tfName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == KeyEvent.VK_SPACE) {
                    e.consume();  // 忽略此字符，阻止输入空格
                }
                if (c < 0 || c > 127) {
                    e.consume();  // 如果字符不在ASCII范围内，忽略它
                }
            }
        });
        add(tfName);

        JLabel key = new JLabel("Enter Your Key");
        key.setBounds(790, 400, 300, 20);
        key.setFont(new Font("Mongolian Baiti", Font.BOLD, 22));
        key.setForeground(Color.BLACK);
        add(key);

        tfKey = new JTextField();
        tfKey.setBounds(790, 440, 300, 25);
        tfKey.setFont(new Font("Times New Roaman", Font.BOLD, 20));
        tfKey.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == KeyEvent.VK_SPACE) {
                    e.consume();  // 忽略此字符，阻止输入空格
                }
                if (c < 0 || c > 127) {
                    e.consume();  // 如果字符不在ASCII范围内，忽略它
                }
            }
        });
        add(tfKey);

        decode = new JButton("Encode");
        decode.setBounds(870, 550, 140, 50);
        Font buttonFont = new Font("Times New Roman", Font.BOLD, 16); // 设置字体大小
        decode.setFont(buttonFont); // 应用字体
        decode.setBackground(Color.WHITE);
        decode.setForeground(Color.BLACK);
        decode.addActionListener(this);
        add(decode);

        encryptionToggle = new JCheckBox("Encryption Mode");
        encryptionToggle.setBounds(870, 480, 140, 50);
        Font ToggleFont = new Font("Times New Roman", Font.BOLD, 14);
        encryptionToggle.setFont(ToggleFont);
        encryptionToggle.setSelected(true); // 默认为加密模式
        encryptionToggle.setBackground(Color.ORANGE);
        encryptionToggle.setOpaque(true);
        encryptionToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (encryptionToggle.isSelected()) {
                    name.setText("Enter Your Plain Text");
                    decode.setText("Encode");
                } else {
                    name.setText("Enter Your Cipher Text");
                    decode.setText("Decode");
                }
            }
        });
        add(encryptionToggle);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    public boolean isStr(String str){
        if (str.length()!=16) return true;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i)!='1'&&str.charAt(i)!='0') return true;
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

    //里面调用算法
    public void actionPerformed(ActionEvent e) {
        String str1 = this.tfName.getText().trim();
        String str2 = this.tfKey.getText().trim();
        if (str1.isEmpty() || str2.isEmpty()) {
            JOptionPane.showMessageDialog(null, "The input field cannot be empty!", "Warning!", JOptionPane.WARNING_MESSAGE);
        } else {
            if (encryptionToggle.isSelected()) {
                // 调用加密算法，上面str1,str2，是算法的两个参数，来自输入框
                S_AES sAes = new S_AES();
                String result;
                if (isStr(str1)) result = sAes.encrypt(str1, str2);
                else result = Arrays.toString(sAes.encrypt(toIntArray(str1),toIntArray(str2)));

                //第三个参数接受加密后得到的字符串
                new ResultDialog(this, "Encode Result", result).setVisible(true);
            } else {
                S_AES sAes = new S_AES();
                String result;
                if (isStr(str1)) result = sAes.decrypt(str1, str2);
                else result = Arrays.toString(sAes.decrypt(toIntArray(str1),toIntArray(str2)));

                //第三个参数
                new ResultDialog(this, "Decode Result", result).setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        S_AES_PAGE l = new S_AES_PAGE();
    }
}