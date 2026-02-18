package oj.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {

    public static String md5(String input) {
        if (input == null) {
            throw new IllegalArgumentException("输入字符串不能为空");
        }
        // 直接调用 DigestUtils.md5Hex() 方法，返回 32 位小写十六进制
        return DigestUtils.md5Hex(input);
    }

}