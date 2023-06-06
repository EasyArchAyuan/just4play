package com.example.ayuan.license.pojo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Ayuan
 * @Description: 校验参数
 * @date 2023/5/25 12:46
 */
@Data
@Builder
public class ValidateParams implements Serializable {

    /**
     * 最后校验时间
     */
    private Long lastValidateTime;

    /**
     * Mac地址
     */
    private String macAddress;

    /**
     * CPU序列号
     */
    private String cpuSerial;

    /**
     * 生成时间
     */
    private Long generatedTime;

    /**
     * 过期时间
     */
    private Long expiredTime;

    /**
     * 版本号
     */
    private String version;
}
