package com.example.ayuan;


import com.example.ayuan.async.AsyncInitBeanFactoryPostProcessor;
import com.example.ayuan.async.AsyncProxyBeanPostProcessor;
import com.example.ayuan.async.AsyncTaskExecutionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Test runtime-sofa-boot-core, build some necessary beans.
 */
@Configuration(proxyBeanMethods = false)
public class SofaRuntimeTestConfiguration {
    @Bean
    public AsyncProxyBeanPostProcessor asyncProxyBeanPostProcessor() {
        return new AsyncProxyBeanPostProcessor();
    }

    @Bean
    public AsyncTaskExecutionListener asyncTaskExecutionListener() {
        return new AsyncTaskExecutionListener();
    }

    @Bean
    public static AsyncInitBeanFactoryPostProcessor asyncInitBeanFactoryPostProcessor() {
        return new AsyncInitBeanFactoryPostProcessor();
    }

}