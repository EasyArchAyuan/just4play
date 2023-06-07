package com.example.ayuan.aop;


import cn.hutool.json.JSONUtil;
import com.example.ayuan.exception.ThreeException;
import com.example.ayuan.license.DaemonProcess;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class ControllerAspect {


    /**
     * 定义切入点为所有controller包下的所有类
     * execution表达式第一个*表示匹配任意的方法返回值，第二个*表示所有controller包下的类，第三个*表示所有方法,第一个..表示任意参数个数。
     */
    @Pointcut("execution(public * com.example.ayuan.controller.*.*(..))")
    public void pointcut() {
    }


    /**
     * 方法执行之前先执行校验License
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        //获取请求的方法
        Signature sig = joinPoint.getSignature();
        String method = joinPoint.getTarget().getClass().getName() + "." + sig.getName();
        //获取请求的参数
        Object[] args = joinPoint.getArgs();
        //fastjson转换
        String params = JSONUtil.toJsonStr(args);
        //打印请求参数
        log.info("请求方法:{} ,请求参数:{}", method, params);

        this.checkLicense();
    }

    /**
     * 校验License证书是否有效
     */
    private void checkLicense() {
        Pair<Boolean, String> booleanStringPair = DaemonProcess.validateAfterUpdateSign();
        if (!booleanStringPair.getKey()) {
            throw new ThreeException(booleanStringPair.getValue());
        }
    }

}

