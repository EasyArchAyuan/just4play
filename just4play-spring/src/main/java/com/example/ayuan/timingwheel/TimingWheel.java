package com.example.ayuan.timingwheel;

import java.util.concurrent.DelayQueue;

/**
 * @Author: Ayuan
 * @Description: 多层时间轮，常用于延时任务
 * 时间轮是一种环形数据结构，由多个槽组成，每个槽中存放任务集合
 * 一个单独的线程推进时间一槽一槽的移动，并执行槽中的任务
 */
public class TimingWheel {

    /**
     * 一个时间槽的范围
     */
    private long tickMs;

    /**
     * 时间轮大小,时间轮中时间槽的个数
     */
    private int wheelSize;

    /**
     * 时间跨度,当前时间轮总间隔，即单个槽的跨度*槽个数
     */
    private long interval;

    /**
     * 时间槽
     */
    private TimerTaskList[] timerTaskLists;


    /**
     * 当前时间,指向当前操作的时间格，代表当前时间
     */
    private long currentTime;

    /**
     * 上层时间轮
     */
    private volatile TimingWheel overflowWheel;

    /**
     * 一个Timer只有一个delayQueue
     */
    private DelayQueue<TimerTaskList> delayQueue;

    public TimingWheel(long tickMs, int wheelSize, long currentTime, DelayQueue<TimerTaskList> delayQueue) {
        this.tickMs = tickMs;
        this.wheelSize = wheelSize;
        this.interval = tickMs * wheelSize;
        this.timerTaskLists = new TimerTaskList[wheelSize];
        //currentTime为tickMs的整数倍 这里做取整操作
        this.currentTime = currentTime - (currentTime % tickMs);
        this.delayQueue = delayQueue;
        for (int i = 0; i < wheelSize; i++) {
            timerTaskLists[i] = new TimerTaskList();
        }
    }

    /**
     * 创建或者获取上层时间轮
     */
    private TimingWheel getOverflowWheel() {
        if (overflowWheel == null) {
            synchronized (this) {
                if (overflowWheel == null) {
                    //父时间轮的刻度是本时间轮的interval也就是全部时间范围，刻度数量保持不变
                    overflowWheel = new TimingWheel(interval, wheelSize, currentTime, delayQueue);
                }
            }
        }
        return overflowWheel;
    }

    /**
     * 添加任务到时间轮
     */
    public boolean addTask(TimerTask timerTask) {
        long expiration = timerTask.getDelayMs();
        //过期任务直接执行
        if (expiration < currentTime + tickMs) {
            return false;
        } else if (expiration < currentTime + interval) {
            //当前时间轮可以容纳该任务 加入时间槽
            long virtualId = expiration / tickMs;
            int index = (int) (virtualId % wheelSize);
            System.out.println("tickMs:" + tickMs + "------index:" + index + "------expiration:" + expiration);
            TimerTaskList timerTaskList = timerTaskLists[index];
            timerTaskList.addTask(timerTask);
            //向下取整只表示刻度，所以在同一刻度下只会放入延迟队列一次
            if (timerTaskList.setExpiration(virtualId * tickMs)) {
                //添加到delayQueue中，相同刻度只会放一次
                delayQueue.offer(timerTaskList);
            }
        } else {
            //放到上一层的时间轮
            TimingWheel timeWheel = getOverflowWheel();
            timeWheel.addTask(timerTask);
        }
        return true;
    }

    /**
     * 推进时间
     */
    public void advanceClock(long timestamp) {
        //对于最小时间轮来说,因为时间往前走了一个刻度,所以timestamp至少等于currentTimestamp + tickMs,
        //如果delayqueue.poll跳过几个没有挂载数据的刻度的话，那么timestamp 大于currentTimestamp + tickMs
        // 但不管怎样，只要poll 出bucket后，currentTimestamp 就会和当前时间保持相对一致的，也算是一直懒处理吧
        if (timestamp >= currentTime + tickMs) {
            currentTime = timestamp - (timestamp % tickMs);
            //尝试更新父时间轮,这个操作会只要overflowWheel不为null就会触发
            //但父currentTimestamp 不一定会改变，因为子时间轮一次20m前进，要走10次，才能到达父时间轮tickMs
            if (overflowWheel != null) {
                //推进上层时间轮时间
                this.getOverflowWheel().advanceClock(timestamp);
            }
        }
    }
}
