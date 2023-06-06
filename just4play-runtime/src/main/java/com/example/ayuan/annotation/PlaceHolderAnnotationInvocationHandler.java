package com.example.ayuan.annotation;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class PlaceHolderAnnotationInvocationHandler implements InvocationHandler {

    private final Annotation delegate;

    private final PlaceHolderBinder binder;

    private PlaceHolderAnnotationInvocationHandler(Annotation delegate, PlaceHolderBinder binder) {
        this.delegate = delegate;
        this.binder = binder;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(delegate, args);
        if (!ReflectionUtils.isEqualsMethod(method) && !ReflectionUtils.isHashCodeMethod(method)
                && !ReflectionUtils.isToStringMethod(method) && isAttributeMethod(method)) {
            return resolvePlaceHolder(ret);
        }
        return ret;
    }

    private boolean isAttributeMethod(Method method) {
        return method != null && method.getParameterTypes().length == 0
                && method.getReturnType() != void.class;
    }

    public Object resolvePlaceHolder(Object origin) {
        if (origin.getClass().isArray()) {
            int length = Array.getLength(origin);
            Object ret = Array.newInstance(origin.getClass().getComponentType(), length);
            for (int i = 0; i < length; ++i) {
                Array.set(ret, i, resolvePlaceHolder(Array.get(origin, i)));
            }
            return ret;
        } else {
            return doResolvePlaceHolder(origin);
        }
    }

    private Object doResolvePlaceHolder(Object origin) {
        if (origin instanceof String) {
            return binder.bind((String) origin);
        } else if (origin instanceof Annotation && !(origin instanceof WrapperAnnotation)) {
            return AnnotationWrapperBuilder.wrap(origin).withBinder(binder).build();
        } else {
            return origin;
        }
    }

    public static class AnnotationWrapperBuilder<A> {
        private Annotation delegate;
        private PlaceHolderBinder binder;

        private AnnotationWrapperBuilder() {
        }

        public static <A> AnnotationWrapperBuilder wrap(A annotation) {
            Assert.isTrue(annotation == null || annotation instanceof Annotation,
                    "Parameter must be annotation type.");
            AnnotationWrapperBuilder<A> builder = new AnnotationWrapperBuilder<A>();
            builder.delegate = (Annotation) annotation;
            return builder;
        }

        public AnnotationWrapperBuilder withBinder(PlaceHolderBinder binder) {
            this.binder = binder;
            return this;
        }

        @SuppressWarnings("unchecked")
        public A build() {
            if (delegate != null) {
                ClassLoader cl = this.getClass().getClassLoader();
                Class<?>[] exposedInterface = {delegate.annotationType(), WrapperAnnotation.class};
                return (A) Proxy.newProxyInstance(cl, exposedInterface,
                        new PlaceHolderAnnotationInvocationHandler(delegate, binder));
            }
            return null;
        }
    }
}
