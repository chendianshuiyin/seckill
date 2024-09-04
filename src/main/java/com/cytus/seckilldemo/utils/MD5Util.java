package com.cytus.seckilldemo.utils;

import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {
    private static final String salt = "1a2b3c4d";

    public static String md5(String src)
    {
        return DigestUtils.md5Hex(src);
    }
    // 第一次加密，输入密码转成后端接收密码
    public static String inputPassFromPass(String inputPass)
    {
        // 第一次加密在前端加密，第一次使用的salt是和前端约定好的
        // 密码在后端解密后再加密
        // 防止salt泄露，只使用部分salt
        String str = "" + salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }
    // 后端密码到数据库的密码
    public static String DBPassFromPass(String fromPass, String salt)
    {
        // 第二次是后端加密，salt将会存到数据库中
        String str  = ""+salt.charAt(0)+salt.charAt(2)+fromPass+salt.charAt(5)+salt.charAt(4);
        return  md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt)
    {
        String fromPass = inputPassFromPass(inputPass);
        String dbPass = DBPassFromPass(fromPass,salt);
        return  dbPass;
    }

    // 测试
    public static void main(String[] args)
    {
        System.out.println(inputPassFromPass("123456"));
        System.out.println(DBPassFromPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }
}
