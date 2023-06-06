package com.example.ayuan.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Ayuan
 * @Description: 测试A
 * @date 2023/6/5 12:18
 */
@Slf4j
public class AAABean {

    public void init() throws InterruptedException {
        log.info("AAABean start Thread.currentThread().getName() = {}", Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(5);
        log.info("AAABean end Thread.currentThread().getName() = {}", Thread.currentThread().getName());

    }
}
