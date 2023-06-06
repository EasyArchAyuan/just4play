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
public class BeanA implements EnvironmentAware, InitializingBean {

    @Autowired
    private BeanC beanC;


    public BeanA() {
        System.out.println("constructA");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("afterA:" + Thread.currentThread().getName() + ",id:" + Thread.currentThread().getId());
    }

    @Resource
    public void resource(Environment environment) {
        System.out.println("resourceA");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("postConstructA:" + Thread.currentThread().getName() + ",id:" + Thread.currentThread().getId());
    }

    @Override
    public void setEnvironment(Environment environment) {
        System.out.println("EnvironmentA");
    }

    public void init() {
        System.out.println("InitA");
    }
}
