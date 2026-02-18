package oj.util;

public class StringUtils {
    private String trimTrailingWhitespace(String str) {
        if (str == null) {
            return "";
        }
        int len = str.length();
        while (len > 0) {
            char c = str.charAt(len - 1);
            if (c == ' ' || c == '\n' || c == '\r') {
                len--;
            } else {
                break;
            }
        }
        return str.substring(0, len);
    }
}
