package com.example.ayuan.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Ayuan
 * @Description: TODO
 * @date 2023/6/6 16:59
 */
@Component
@Configuration
public class BeanB implements EnvironmentAware, InitializingBean {

    @Autowired
    private BeanC beanC;

    public BeanB() {
        System.out.println("constructB");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("afterB:" + Thread.currentThread().getName() + ",id:" + Thread.currentThread().getId());
    }

    @Resource
    public void resource(Environment environment) {
        System.out.println("resourceB");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("postConstructB:" + Thread.currentThread().getName() + ",id:" + Thread.currentThread().getId());
    }

    @Override
    public void setEnvironment(Environment environment) {
        System.out.println("EnvironmentB");
    }

    public void init() {
        System.out.println("InitB");
    }
}
