package com.example.ayuan.license.pojo;

import lombok.Data;

/**
 * @author Ayuan
 * @Description: 校验结果
 * @date 2023/5/25 12:43
 */
@Data
public class ValidateResult {
    /**
     * 校验结果
     */
    private Boolean success;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 校验结果信息
     */
    private String message;


    public static ValidateResult success() {
        ValidateResult result = new ValidateResult();
        result.setSuccess(true);
        result.setCode(ValidateCodeEnum.SUCCESS.getCode());
        result.setMessage(ValidateCodeEnum.SUCCESS.getMessage());
        return result;
    }

    public static ValidateResult failed(ValidateCodeEnum codeEnum) {
        ValidateResult result = new ValidateResult();
        result.setSuccess(false);
        result.setCode(codeEnum.getCode());
        result.setMessage(codeEnum.getMessage());
        return result;
    }

}
