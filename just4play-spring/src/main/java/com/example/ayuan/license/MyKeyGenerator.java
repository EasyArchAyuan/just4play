package com.example.ayuan.license;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * @author Ayuan
 * @Description: 公钥和私钥的生成器
 * @date 2023/5/25 17:18
 */
public class MyKeyGenerator {

    /**
     * 私钥
     */
    private static byte[] privateKey;
    /**
     * 公钥
     */
    private static byte[] publicKey;
    /**
     * 加密算法
     */
    private static final String KEY_ALGORITHM = "RSA";


    public void generator() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            RSAPublicKey pubKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey priKey = (RSAPrivateKey) keyPair.getPrivate();
            privateKey = Base64.getEncoder().encode(priKey.getEncoded());
            publicKey = Base64.getEncoder().encode(pubKey.getEncoded());
            System.out.println("公钥：" + new String(publicKey));
            System.out.println("私钥：" + new String(privateKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("生成密钥对失败！");
        }
    }


    public static void main(String[] args) {
        MyKeyGenerator keyGenerator = new MyKeyGenerator();
        keyGenerator.generator();
    }



}
