package com.example.ayuan;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class TimeWasteBean {
    private long                       printTime;
    private String                     threadName;
    private static final AtomicInteger count = new AtomicInteger(0);

    public void init() throws Exception {
        printTime = System.currentTimeMillis();
        threadName = Thread.currentThread().getName();
        TimeUnit.SECONDS.sleep(1);
        count.getAndIncrement();
    }

    public long getPrintTime() {
        return printTime;
    }

    public static int getCount() {
        return count.get();
    }

    public String getThreadName() {
        return threadName;
    }

    public static void resetCount() {
        count.set(0);
    }
}