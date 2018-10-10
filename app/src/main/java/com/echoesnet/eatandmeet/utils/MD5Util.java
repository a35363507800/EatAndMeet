/**
 * SunlightFrame
 * <p>
 * (C) Copyright sunlightcloud.com
 * 本内容仅限于sunlightcloud.com授权使用，未经授权不得用于商业用途。谢谢合作！
 */
package com.echoesnet.eatandmeet.utils;

import android.text.TextUtils;

import java.security.MessageDigest;

/**
 * MD5加密工具。
 */
public class MD5Util
{
    /**
     * 对字符串进行加密。
     *
     * @param sourceString 加密前字符串。
     * @return 加密后字符串。
     * @throws Exception
     */
    public static String MD5(String sourceString, String encoding) throws Exception
    {
        if (TextUtils.isEmpty(sourceString))
        {
            return "null";
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buffer = sourceString.getBytes(encoding);
        byte[] digestBytes = md.digest(buffer);
        return bytes2Hex(digestBytes).toLowerCase();
    }

    public static String MD5(String sourceString) throws Exception
    {
        if (TextUtils.isEmpty(sourceString))
        {
            return "null";
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buffer = sourceString.getBytes();
        byte[] digestBytes = md.digest(buffer);
        return bytes2Hex(digestBytes).toLowerCase();
    }

    private static String bytes2Hex(byte[] bts)
    {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++)
        {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1)
            {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }
}
