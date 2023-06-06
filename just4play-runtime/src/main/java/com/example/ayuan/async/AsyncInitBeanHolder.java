package com.example.ayuan.async;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class AsyncInitBeanHolder {
    private static final ConcurrentMap<String, Map<String, String>> asyncBeanInfos = new ConcurrentHashMap<>();

    public static void registerAsyncInitBean(String moduleName, String beanId, String methodName) {
        if (moduleName == null || beanId == null || methodName == null) {
            return;
        }

        Map<String, String> asyncBeanInfosInModule = asyncBeanInfos.get(moduleName);
        if (asyncBeanInfosInModule == null) {
            asyncBeanInfos.putIfAbsent(moduleName, new ConcurrentHashMap<>());
            asyncBeanInfosInModule = asyncBeanInfos.get(moduleName);
        }

        asyncBeanInfosInModule.put(beanId, methodName);
    }

    public static String getAsyncInitMethodName(String moduleName, String beanId) {
        Map<String, String> asyncBeanInfosInModule;
        asyncBeanInfosInModule = (moduleName == null) ? null : asyncBeanInfos.get(moduleName);
        return (beanId == null || asyncBeanInfosInModule == null) ? null : asyncBeanInfosInModule
            .get(beanId);
    }
}
