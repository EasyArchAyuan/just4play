package com.example.ayuan.license;

import com.example.ayuan.license.pojo.ValidateCodeEnum;
import com.example.ayuan.license.pojo.ValidateParams;
import com.example.ayuan.license.pojo.ValidateResult;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Ayuan
 * @Description: 许可证管理类
 * @date 2023/5/25 15:53
 */
@Component
public class LicenseManager {
    private static final String AUTHORIZE = "Authorize";
    public static String AES_KEY = "pjbbAvaE8faDOjYvqMPIMQ==";

    /**
     * 验证
     *
     * @return 验证Map
     */
    public static Map<String, ValidateResult> validate() {
        //k: Authorize, v: ValidateResult
        Map<String, ValidateResult> map = new HashMap<>();
        ValidateParams validateParams = null;
        try {
            //读取licence文件
            validateParams = getValidateParams(map);
            if (validateParams == null) {
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put(AUTHORIZE, ValidateResult.failed(ValidateCodeEnum.EXCEPTION));
            return map;
        }
        //校验mac地址
        if (!validateParams.getMacAddress().equals(SystemUtils.getMacAddress())) {
            map.put("Authorize", ValidateResult.failed(ValidateCodeEnum.UNAUTHORIZED));
            return map;
        }
        //校验cpu序列号
        if (!validateParams.getCpuSerial().equals(SystemUtils.getCpuSerialNumber())) {
            map.put("Authorize", ValidateResult.failed(ValidateCodeEnum.UNAUTHORIZED));
            return map;
        }
        long currentTime = System.currentTimeMillis();
        //校验时间
        if ((validateParams.getLastValidateTime() >= currentTime) ||
                (validateParams.getGeneratedTime() <= currentTime) ||
                (validateParams.getExpiredTime() >= currentTime)) {
            map.put("Authorize", ValidateResult.failed(ValidateCodeEnum.EXPIRED));
            return map;
        }
        //校验通过
        map.put(AUTHORIZE, ValidateResult.success());
        return map;
    }


    /**
     * 获得许可
     *
     * @return license sign
     */
    public static String getLicense() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String macAddress = SystemUtils.getMacAddress();
        String cpuSerialNumber = SystemUtils.getCpuSerialNumber();
        return AESUtils.encrypt(macAddress + "-" + cpuSerialNumber, AES_KEY);
    }

    /**
     * 更新许可
     *
     * @param sign 新许可
     */
    public static void updateSign(String sign) {
        try {
            Document document = readLicense();
            Element rootElement = Objects.requireNonNull(document).getRootElement();
            Element signatureEle = rootElement.element("signature");
            signatureEle.setText(sign);
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");
            String path = System.getProperty("user.dir");
            FileWriter fileWriter = new FileWriter(path + File.separator + "license.xml");
            XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
            // 设置是否转义，默认使用转义字符
            xmlWriter.setEscapeText(false);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新授权码失败！");
        }
    }


    /**
     * 读取license证书
     */
    private static Document readLicense() {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            String path = System.getProperty("user.dir");
            document = saxReader.read(new File(path + File.separator + "license.xml"));
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static ValidateParams getValidateParams(Map<String, ValidateResult> map) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Document document = readLicense();
        if (document == null) {
            map.put("Authorize", ValidateResult.failed(ValidateCodeEnum.FILE_NOT_EXIST));
            return null;
        }
        Element rootElement = document.getRootElement();
        Element dataEle = rootElement.element("features");
        List<Element> featuresEles = dataEle.elements();
        Element lastValidateTimeEle = featuresEles.get(0);
        //提取上一次验证时间
        String lastValidateTimeStr = lastValidateTimeEle.attributeValue("ti");
        long lastValidateTime = Long.parseLong(AESUtils.decrypt(lastValidateTimeStr, AES_KEY));
        //提取签名内容
        Element signEle = rootElement.element("signature");
        String signStr = signEle.getText();
        String sign = AESUtils.decrypt(signStr, AES_KEY);
        String[] signArr = sign.split("-");
        if (signArr.length != 5) {
            map.put("Authorize", ValidateResult.failed(ValidateCodeEnum.ILLEGAL));
            return null;
        }
        return ValidateParams.builder().lastValidateTime(lastValidateTime).macAddress(signArr[0])
                .cpuSerial(signArr[1]).generatedTime(Long.parseLong(signArr[2])).expiredTime(Long.parseLong(signArr[3]))
                .version(signArr[4]).build();
    }

}
