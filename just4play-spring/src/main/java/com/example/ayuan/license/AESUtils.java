package com.example.ayuan.license;

import org.springframework.util.Base64Utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Ayuan
 * @Description: AES工具类
 * @date 2023/5/25 12:53
 */
public class AESUtils {
    public static String AES_KEY = "pjbbAvaE8faDOjYvqMPIMQ==";
    //AES_KEY一般是约定的

    public static void main(String[] args) throws Exception {
        //生成AES_KEY
        String aesKey = generateKey();
        System.out.println("随机生成AES_KEY:"+aesKey);
        //加密调用例子
        String str = "11111111";
        //原始数据
        String resultStr = AESUtils.encrypt(str);
        System.out.println("AES128加密后:"+resultStr);
        //解密调用例子
        String originalStr = AESUtils.decrypt(resultStr);
        System.out.println("AES128解密后--原始数据:"+originalStr);
    }

    /**
     * 随机生成AES_KEY
     */
    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey key = generator.generateKey();
        return base64Encode(key.getEncoded());
    }

    /**
     * 加密
     */
    public static String encrypt(String data)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(base64Decode(AES_KEY), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64Encode(result);
    }

    /**
     * 解密
     */
    public static String decrypt(String data)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(base64Decode(AES_KEY), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] result = cipher.doFinal(base64Decode(data));
        return new String(result, StandardCharsets.UTF_8);
    }

    private static String base64Encode(byte[] src) {
        return Base64Utils.encodeToString(src);
    }

    private static byte[] base64Decode(String src) {
        return Base64Utils.decodeFromString(src);
    }


}
