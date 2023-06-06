package com.example.ayuan.async;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;


public class AsyncTaskExecutionListener implements PriorityOrdered,
                                       ApplicationListener<ContextRefreshedEvent>,
                                       ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (applicationContext.equals(event.getApplicationContext())) {
            AsyncTaskExecutor.ensureAsyncTasksFinish();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
