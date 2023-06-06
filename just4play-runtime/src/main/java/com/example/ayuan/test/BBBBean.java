package com.example.ayuan.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Ayuan
 * @Description: 测试B
 * @date 2023/6/5 12:18
 */
@Slf4j
public class BBBBean {

    public void init() throws InterruptedException {
        log.info("BBBBean start Thread.currentThread().getName() = {}", Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(5);
        log.info("BBBBean end Thread.currentThread().getName() = {}", Thread.currentThread().getName());

    }
}
