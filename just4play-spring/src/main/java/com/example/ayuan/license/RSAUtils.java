package com.example.ayuan.license;

import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 1.Rsa签名:MD5加密+base64Encode
 * 2.生成密钥对(公钥、私钥)
 * 3.SHA-256加密
 *
 * @author Ayuan
 * org.springframework.security.crypto.factory.PasswordEncoderFactories包含了加密的各种架包，可直接使用
 */
@Component
public class RSAUtils {

    private final String KEY_ALGORITHM = "RSA";
    private final String SIGN_ALGORITHM = "MD5withRSA";
    private final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKOFZwG/JnG4U7j2efaz87G2URl+ptFCFJKyIAq9YDUKR8xzbp3s8OJeUN2D3LnFkyZTKRTbc5ckHJd4vUXNbw23znt2KVLKuoLkvDTEJuswxkt8/8f8SGeAv4lSyN9S6BA/Y/8jN+tuprla7bgECrkV9NnnSrNHU5+AUl6lBE8NAgMBAAECgYAJtf5sCCIMVtoB/gE4OW+beixOy0q02qvGGnX0rVJtU/L3nVNQZaXYi6lZvl75WVZnzECAUJgIvchE5pK29oBFw2u8/0jVLPD2ep0cFinaLkjb1ut/jz4J/vQ4PHg78af2O+pzGGKgfVPLnN0zbTDTHPMs1Ymwr5mzALZjP5hyAQJBAP2S6CT/jtf8I8pihEjEF5Iy6gLnyAuLbfhJwjV8MihJRHEcIgMroYmehOXqACQ5gDYYaXTsiXiKC8qUwtlgQuECQQClFeyS0ZhrXh0Xe7cbfR1HjTCN3r4/TRL9IeakPab9or/5YoCW246RWKcggTMc10Dp+LyzMDyqldZhMWB3d72tAkBePsm1Zp6KvBX5VKBiAy/XkMDVD5yUXeAjlhZulph1zLV5bMFfeEnzwk0WvuAKlqyGbpBTes6lVHmJc7zv2g1BAkEAovUFVdg3kKSSKwAgO65BHMQuTZy2R14ZhG3WcgG5uVzSC6ZEMKYCEU9lihx/C9UfatXxzx+qgujteXt4MfWs8QJAZIslGx+bxZCxUBXhg7tyI2dm7M24LEYswRaUSOIVoBNAogRzE7dGv2dEAhqHrZQF3SC7H8ZEXE4GysvTyDCQFg==";
    private final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjhWcBvyZxuFO49nn2s/OxtlEZfqbRQhSSsiAKvWA1CkfMc26d7PDiXlDdg9y5xZMmUykU23OXJByXeL1FzW8Nt857dilSyrqC5Lw0xCbrMMZLfP/H/EhngL+JUsjfUugQP2P/Izfrbqa5Wu24BAq5FfTZ50qzR1OfgFJepQRPDQIDAQAB";


    /**
     * 私钥签名
     */
    public String sign(byte[] data) throws Exception {
        PrivateKey privateKey = getPrivateKey();
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return base64Encode(signature.sign());
    }

    /**
     * 公钥验签
     */
    public boolean verify(byte[] data, String sign) throws Exception {
        PublicKey publicKey = getPublicKey();
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(base64Decode(sign));
    }

    private String base64Encode(byte[] src) {
        return Base64Utils.encodeToString(src);
    }

    private byte[] base64Decode(String src) {
        return Base64Utils.decodeFromString(src);
    }

    private PrivateKey getPrivateKey() throws Exception {
        byte[] key = base64Decode(PRIVATE_KEY);//PRIVATE_KEY可以随机生成;也可以约定定值
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    private PublicKey getPublicKey() throws Exception {
        byte[] key = base64Decode(PUBLIC_KEY);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 生成密钥对(公钥、私钥)
     */
    public String generateKeyPair() throws Exception {
        Map<String, String> keyMap = new HashMap<String, String>();
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        System.out.println("PUBLIC_KEY=" + base64Encode(publicKey.getEncoded()));
        System.out.println("PRIVATE_KEY=" + base64Encode(privateKey.getEncoded()));
        return "PUBLIC_KEY=" + base64Encode(publicKey.getEncoded()) + ";PRIVATE_KEY=" + base64Encode(privateKey.getEncoded());
    }

    //SHA-256加密
    public String getSHA256StrJava(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    private String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}
