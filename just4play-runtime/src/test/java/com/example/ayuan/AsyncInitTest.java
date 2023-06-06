package com.example.ayuan;


import com.example.ayuan.annotation.BeanAsyncInit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AsyncInitTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testAsyncInitBean() {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        Assert.assertEquals(15, TimeWasteBean.getCount());
        for (int i = 1; i <= 12; i++) {
            TimeWasteBean bean = ctx.getBean("testBean" + i, TimeWasteBean.class);
            if (bean.getPrintTime() < min) {
                min = bean.getPrintTime();
            }
            if (bean.getPrintTime() > max) {
                max = bean.getPrintTime();
            }
            String threadName = bean.getThreadName();
            Assert.assertTrue(threadName, threadName.contains("async-init-bean"));
        }
        for (int i = 13; i <= 15; i++) {
            TimeWasteBean bean = ctx.getBean("testBean" + i, TimeWasteBean.class);
            String threadName = bean.getThreadName();
            Assert.assertFalse(threadName, threadName.contains("async-init-bean"));
        }
        Assert.assertTrue("max:" + max + ", min:" + min, max - min < 5000);
        TimeWasteBean.resetCount();
    }

    @Configuration(proxyBeanMethods = false)
    @ImportResource({ "classpath*:META-INF/async/*.xml" })
    @Import(SofaRuntimeTestConfiguration.class)
    static class AsyncInitTestConfiguration {

        @Bean(initMethod = "init")
        @BeanAsyncInit
        public TimeWasteBean testBean12() {
            return new TimeWasteBean();
        }

        @Bean(initMethod = "init")
        @BeanAsyncInit(value = false)
        public TimeWasteBean testBean13() {
            return new TimeWasteBean();
        }

    }

    @BeanAsyncInit
    static class TimeWasteBeanChild extends TimeWasteBean {

    }
}