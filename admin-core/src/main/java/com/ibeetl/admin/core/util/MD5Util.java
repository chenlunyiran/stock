/**
 * Twotiger.com Llc.
 * Copyright (c) 2013-2014 All Rights Reserved.
 */
package com.ibeetl.admin.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {

    public static String MD5(String pwd) {
        try {
            byte[] btInput = pwd.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            return byteArrayToHex(md);

        } catch (Exception e) {
            return null;
        }
    }

    public static String byteArrayToHex(byte[] byteArray) {
        //用于加密的字符
        char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
                            'E', 'F' };

        int j = byteArray.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) { //  i = 0
            byte byte0 = byteArray[i]; //95
            str[k++] = md5String[byte0 >>> 4 & 0xf]; //    5  
            str[k++] = md5String[byte0 & 0xf]; //   F
        }

        //返回经过加密后的字符串
        return new String(str);
    }
    
}
