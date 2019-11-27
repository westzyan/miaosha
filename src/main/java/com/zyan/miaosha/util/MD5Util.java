package com.zyan.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-22 下午4:30
 */
public class MD5Util {
    public static String md5(String str){

        return DigestUtils.md5Hex(str);
    }

    private static final String salt = "1a2b3c4d";
    public static String inputPassToFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String inputPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }


    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456"));
        System.out.println(formPassToDBPass(inputPassToFormPass("123456"),"1a2b3c4d"));
        System.out.println(new Date().toString());
    }
}
