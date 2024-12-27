package com.burukeyou.uniapi.util;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

public class EncodeUtil {

    private EncodeUtil(){}

    public static String base64Decode(String content) {
        if (StringUtils.isBlank(content)){
            return content;
        }
        try {
            content = content.trim();
            return new String(Base64.getDecoder().decode(content.getBytes()));
        } catch (Exception e) {
            return content;
        }
    }

    public static byte[] base64DecodeToByte(String content) {
        if (StringUtils.isBlank(content)){
            return new byte[0];
        }
        try {
            content = content.trim();
            return Base64.getDecoder().decode(content.getBytes());
        } catch (Exception e) {
            return content.getBytes();
        }
    }
}
