package com.example.ayuan.async;

import com.example.ayuan.annotation.BeanAsyncInit;
import com.example.ayuan.annotation.PlaceHolderAnnotationInvocationHandler;
import com.example.ayuan.annotation.PlaceHolderBinder;
import com.example.ayuan.util.BeanDefinitionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class AsyncInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor,
        ApplicationContextAware, EnvironmentAware {

    private final PlaceHolderBinder binder = new DefaultPlaceHolderBinder();
    private Environment environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Arrays.stream(beanFactory.getBeanDefinitionNames())
                .collect(Collectors.toMap(Function.identity(), beanFactory::getBeanDefinition))
                .forEach((key, value) -> scanAsyncInitBeanDefinition(key, value, beanFactory));
    }

    /**
     * {@link ScannedGenericBeanDefinition}
     * {@link AnnotatedGenericBeanDefinition}
     * {@link GenericBeanDefinition}
     * {@link org.springframework.beans.factory.support.ChildBeanDefinition}
     * {@link org.springframework.beans.factory.support.RootBeanDefinition}
     */
    private void scanAsyncInitBeanDefinition(String beanId, BeanDefinition beanDefinition,
                                             ConfigurableListableBeanFactory beanFactory) {
        if (BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)) {
            scanAsyncInitBeanDefinitionOnMethod(beanId, (AnnotatedBeanDefinition) beanDefinition);
        } else {
            Class<?> beanClassType = BeanDefinitionUtil.resolveBeanClassType(beanDefinition);
            if (beanClassType == null) {
                log.warn("Bean class type cant be resolved from bean of {}", beanId);
                return;
            }
            scanAsyncInitBeanDefinitionOnClass(beanId, beanClassType, beanDefinition, beanFactory);
        }
    }

    private void scanAsyncInitBeanDefinitionOnMethod(String beanId,
                                                     AnnotatedBeanDefinition beanDefinition) {
        Class<?> returnType;
        Class<?> declaringClass;
        List<Method> candidateMethods = new ArrayList<>();

        MethodMetadata methodMetadata = beanDefinition.getFactoryMethodMetadata();
        try {
            returnType = ClassUtils.forName(methodMetadata.getReturnTypeName(), null);
            declaringClass = ClassUtils.forName(methodMetadata.getDeclaringClassName(), null);
        } catch (Throwable throwable) {
            // it's impossible to catch throwable here
            log.error("报错啦！", throwable);
            return;
        }
        if (methodMetadata instanceof StandardMethodMetadata) {
            candidateMethods.add(((StandardMethodMetadata) methodMetadata).getIntrospectedMethod());
        } else {
            for (Method m : declaringClass.getDeclaredMethods()) {
                // check methodName and return type
                if (!m.getName().equals(methodMetadata.getMethodName())
                        || !m.getReturnType().getTypeName().equals(methodMetadata.getReturnTypeName())) {
                    continue;
                }

                // check bean method
                if (!AnnotatedElementUtils.hasAnnotation(m, Bean.class)) {
                    continue;
                }

                Bean bean = m.getAnnotation(Bean.class);
                Set<String> beanNames = new HashSet<>();
                beanNames.add(m.getName());
                if (bean != null) {
                    beanNames.addAll(Arrays.asList(bean.name()));
                    beanNames.addAll(Arrays.asList(bean.value()));
                }

                // check bean name
                if (!beanNames.contains(beanId)) {
                    continue;
                }

                candidateMethods.add(m);
            }
        }

        if (candidateMethods.size() == 1) {
            BeanAsyncInit beanAsyncInit = candidateMethods.get(0).getAnnotation(
                    BeanAsyncInit.class);
            if (beanAsyncInit == null) {
                beanAsyncInit = returnType.getAnnotation(BeanAsyncInit.class);
            }
            registerAsyncInitBean(beanId, beanAsyncInit, beanDefinition);
        } else if (candidateMethods.size() > 1) {
            for (Method m : candidateMethods) {
                if (AnnotatedElementUtils.hasAnnotation(m, BeanAsyncInit.class)
                        || AnnotatedElementUtils.hasAnnotation(returnType, BeanAsyncInit.class)) {
                    log.error("报错了:{}", declaringClass.getCanonicalName());
                    return;
                }
            }
        }
    }

    private void scanAsyncInitBeanDefinitionOnClass(String beanId, Class<?> beanClass,
                                                    BeanDefinition beanDefinition,
                                                    ConfigurableListableBeanFactory beanFactory) {
        BeanAsyncInit beanAsyncInit = AnnotationUtils.findAnnotation(beanClass, BeanAsyncInit.class);
        registerAsyncInitBean(beanId, beanAsyncInit, beanDefinition);
    }

    @SuppressWarnings("unchecked")
    private void registerAsyncInitBean(String beanId, BeanAsyncInit beanAsyncInit,
                                       BeanDefinition beanDefinition) {
        if (beanAsyncInit == null) {
            return;
        }
        PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder<BeanAsyncInit> wrapperBuilder = PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder
                .wrap(beanAsyncInit).withBinder(binder);
        beanAsyncInit = wrapperBuilder.build();

        if (beanAsyncInit.value()) {
            String moduleName = "EasyArchAyuan";
            AsyncInitBeanHolder.registerAsyncInitBean(moduleName, beanId, beanDefinition.getInitMethodName());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {}

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    class DefaultPlaceHolderBinder implements PlaceHolderBinder {
        @Override
        public String bind(String text) {
            return environment.resolvePlaceholders(text);
        }
    }

}
