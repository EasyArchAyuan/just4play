package com.example.ayuan.async;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 拓展核心：扩展BeanPostProcessor
 */
@Slf4j
public class AsyncProxyBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware,
        InitializingBean, PriorityOrdered {

    private ApplicationContext applicationContext;

    /**
     * 统计Bean加载计时用的
     */
    private Map<String, Long> mapBeantime = new ConcurrentHashMap<>();

    private String moduleName;

    public AsyncProxyBeanPostProcessor() {
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        //统计时间
        mapBeantime.put(beanName, System.currentTimeMillis());
        //正常流程
        String methodName = AsyncInitBeanHolder.getAsyncInitMethodName(moduleName, beanName);
        if (methodName == null || methodName.length() == 0) {
            return bean;
        }

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(bean.getClass());
        proxyFactory.setProxyTargetClass(true);
        AsyncInitializeBeanMethodInvoker asyncInitializeBeanMethodInvoker = new AsyncInitializeBeanMethodInvoker(
                bean, beanName, methodName);
        proxyFactory.addAdvice(asyncInitializeBeanMethodInvoker);
        return proxyFactory.getProxy();
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName)
            throws BeansException {
        Long begin = mapBeantime.get(beanName);
        if (begin != null) {
            mapBeantime.put(beanName, System.currentTimeMillis() - begin);
        }
        return bean;
    }

    @Override
    public void afterPropertiesSet() {
        ConfigurableBeanFactory beanFactory = ((AbstractApplicationContext) applicationContext)
                .getBeanFactory();
//        if (beanFactory instanceof BeanLoadCostBeanFactory) {
//            moduleName = ((BeanLoadCostBeanFactory) beanFactory).getId();
//        } else {
//            moduleName = SofaBootConstants.ROOT_APPLICATION_CONTEXT;
//        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    class AsyncInitializeBeanMethodInvoker implements MethodInterceptor {
        private final Object targetObject;
        private final String asyncMethodName;
        private final String beanName;
        // 是 CountDownLatch 对象，其中 count 初始化为 1
        private final CountDownLatch initCountDownLatch = new CountDownLatch(1);
        // 表示是否正在异步执行 init 方法
        private volatile boolean isAsyncCalling = false;
        // 表示是否已经异步执行过 init 方法
        private volatile boolean isAsyncCalled = false;

        AsyncInitializeBeanMethodInvoker(Object targetObject, String beanName, String methodName) {
            this.targetObject = targetObject;
            this.beanName = beanName;
            this.asyncMethodName = methodName;
        }

        @Override
        public Object invoke(final MethodInvocation invocation) throws Throwable {
            // if the spring refreshing is finished
            if (AsyncTaskExecutor.isStarted()) {
                return invocation.getMethod().invoke(targetObject, invocation.getArguments());
            }

            Method method = invocation.getMethod();
            final String methodName = method.getName();
            if (!isAsyncCalled && methodName.equals(asyncMethodName)) {
                isAsyncCalled = true;
                isAsyncCalling = true;
                AsyncTaskExecutor.submitTask(applicationContext.getEnvironment(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            long startTime = System.currentTimeMillis();
                            invocation.getMethod().invoke(targetObject, invocation.getArguments());
                            log.info(String.format(
                                    "%s(%s) %s method execute %dms, moduleName: %s.", targetObject
                                            .getClass().getName(), beanName, methodName, (System
                                            .currentTimeMillis() - startTime), moduleName));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        } finally {
                            initCountDownLatch.countDown();
                            isAsyncCalling = false;
                        }
                    }
                });
                return null;
            }

            if (isAsyncCalling) {
                long startTime = System.currentTimeMillis();
                initCountDownLatch.await();
                log.info(String.format("%s(%s) %s method wait %dms, moduleName: %s.",
                        targetObject.getClass().getName(), beanName, methodName,
                        (System.currentTimeMillis() - startTime), moduleName));
            }
            return invocation.getMethod().invoke(targetObject, invocation.getArguments());
        }
    }

}
