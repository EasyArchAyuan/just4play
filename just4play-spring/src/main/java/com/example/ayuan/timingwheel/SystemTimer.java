package com.example.ayuan.timingwheel;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Ayuan
 * 对时间轮的包装
 */
public class SystemTimer {

    /**
     * 底层时间轮
     */
    private TimingWheel timeWheel;

    /**
     * 一个Timer只有一个delayQueue
     */
    private DelayQueue<TimerTaskList> delayQueue = new DelayQueue<>();

    /**
     * 过期任务执行线程
     */
    private ExecutorService workerThreadPool;

    /**
     * 轮询delayQueue获取过期任务线程
     */
    private ExecutorService bossThreadPool;

    /**
     * 构造函数
     */
    public SystemTimer() {
        timeWheel = new TimingWheel(1, 20, System.currentTimeMillis(), delayQueue);
        workerThreadPool = Executors.newFixedThreadPool(100);
        bossThreadPool = Executors.newFixedThreadPool(1);
        //20ms获取一次过期任务
        bossThreadPool.submit(() -> {
            while (true) {
                //利用延迟队列阻塞出队的方式模拟时间线的前行，然后执行挂载在 时间刻度下 bucket 的取出
                this.advanceClock(20);
            }
        });
    }

    /**
     * 添加任务
     */
    public void addTask(TimerTask timerTask) {
        //添加失败任务直接执行
        if (!timeWheel.addTask(timerTask)) {
            //如果一个任务 将要执行的时间 小于最小精度了也就是 20ms 了，那还添加啥啊，直接就执行了。
            workerThreadPool.submit(timerTask.getTask());
        }
    }

    /**
     * 推进一下时间轮的指针，并且将delayQueue的任务取出来再重新扔进去
     */
    private void advanceClock(long timeout) {
        try {
            //把到期的timerTaskList取出来，但是这个时间不是任务的具体时间，只是某个时间轮刻度的时间
            TimerTaskList timerTaskList = delayQueue.poll(timeout, TimeUnit.MILLISECONDS);
            if (timerTaskList != null) {
                //能取出来，说明这个timerTask已经过了真实世界的当前时间了，已经过期了
                timeWheel.advanceClock(timerTaskList.getExpiration());
                //执行过期任务（包含降级操作）
                timerTaskList.flush(this::addTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}