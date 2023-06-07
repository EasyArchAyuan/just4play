package com.example.ayuan.license;

import com.example.ayuan.license.pojo.ValidateCodeEnum;
import com.example.ayuan.license.pojo.ValidateResult;
import javafx.util.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Ayuan
 * @Description: 守护线程
 * @date 2023/5/25 12:42
 */
@Component
public class DaemonProcess implements Runnable {

    public static Map<String, ValidateResult> map = new HashMap<>();


    private static final Long checkTime = 30L;


    @Bean
    public void startThread() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }


    @Override
    public void run() {
        while (true) {
            //获取认证结果
            map = LicenseManager.validate();
            if (map != null) {
                ValidateResult result = map.get("Authorize");
                //todo:验证通过修改全局变量boolean值. 如果false了直接拦截返回对应map的错误信息
                System.out.println("license校验结果：" + result.getMessage());
            }
            try {
                //每隔多长时间校验一次
                TimeUnit.SECONDS.sleep(checkTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static Pair<Boolean, String> validateAfterUpdateSign() {
        map = LicenseManager.validate();
        ValidateResult result = map.get("Authorize");
        if (Objects.isNull(result)) {
            result = new ValidateResult();
            result.setCode(ValidateCodeEnum.UNAUTHORIZED.getCode());
            result.setMessage("License验证失败");
        }
        return new Pair<>(result.getSuccess(), result.getMessage());
    }
}
