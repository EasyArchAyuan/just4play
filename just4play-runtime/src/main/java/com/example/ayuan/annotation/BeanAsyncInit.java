package com.example.ayuan.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 用于标记spring bean异步init的注释。示例用法:
 *
 * <pre>
 *
 * &#064;BeanAsyncInit
 * public class SampleServiceImpl implements InitializingBean {
 *
 *     &#064;Override
 *     public void afterPropertiesSet() {
 *         //do something slowly;
 *     }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BeanAsyncInit {

    /**
     * Whether init-method async invoke should occur.
     */
    boolean value() default true;

}
