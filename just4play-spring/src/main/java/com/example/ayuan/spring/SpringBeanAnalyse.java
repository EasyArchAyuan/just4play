package com.example.ayuan.spring;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ayuan
 * @Description: 耗时检测
 * @date 2023/6/6 16:51
 */
@Component
@Slf4j
public class SpringBeanAnalyse implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {
    private Map<String, Long> mapBeantime = new ConcurrentHashMap<>();
    private static volatile AtomicBoolean started = new AtomicBoolean(false);


    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        if (started.compareAndSet(false, true)) {
            for (Map.Entry<String, Long> entry : mapBeantime.entrySet()) {
                if (entry.getValue() > 1000) {
                    System.out.println("slow Spring Bean =>:" + entry.getKey());
                    System.out.println("耗时:" + entry.getValue());
                }
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        mapBeantime.put(beanName, System.currentTimeMillis());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        Long begin = mapBeantime.get(beanName);
        if (begin != null) {
            mapBeantime.put(beanName, System.currentTimeMillis() - begin);
        }
        return bean;
    }
}
