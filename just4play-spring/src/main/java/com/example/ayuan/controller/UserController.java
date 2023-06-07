package com.example.ayuan.controller;

import com.example.ayuan.common.CommonResult;
import com.example.ayuan.domain.User;
import com.example.ayuan.exception.ExceptionCodeEnum;
import com.example.ayuan.license.DaemonProcess;
import com.example.ayuan.license.LicenseManager;
import com.example.ayuan.license.pojo.ValidateResult;
import com.example.ayuan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Ayuan
 * @since 2023-05-24
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    UserService userService;


    @GetMapping("/getServerID")
    public CommonResult<String> getServerID() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String license = LicenseManager.getLicense();
        return CommonResult.success(license);

    }


    @GetMapping("/getUserById")
    public CommonResult<String> getUser() {
        ValidateResult validateResult = DaemonProcess.map.get("Authorize");
        if (!validateResult.getSuccess()) {
            return CommonResult.failed(ExceptionCodeEnum.FAILED, validateResult.getMessage());
        }
        User byId = userService.getById(1);
        return CommonResult.success(byId.getName());
    }

}
