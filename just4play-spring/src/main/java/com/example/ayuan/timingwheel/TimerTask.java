package com.example.ayuan.timingwheel;

/**
 * @Author: Ayuan
 */
public class TimerTask {
    /**
     * 延迟时间
     */
    private long delayMs;

    /**
     * 任务
     */
    private Runnable task;

    /**
     * 时间槽
     */
    protected TimerTaskList timerTaskList;

    /**
     * 下一个节点
     */
    protected TimerTask next;

    /**
     * 上一个节点
     */
    protected TimerTask prev;

    /**
     * 描述
     */
    public String desc;

    public TimerTask(Runnable task, long delayMs) {
        this.delayMs = System.currentTimeMillis() + delayMs;
        this.task = task;
        this.timerTaskList = null;
        this.next = null;
        this.prev = null;
    }

    public Runnable getTask() {
        return task;
    }

    /**
     * 获取延迟时间点，即创建时间+延迟时长（单位毫秒）
     */
    public long getDelayMs() {
        return delayMs;
    }

    @Override
    public String toString() {
        return desc;
    }
}
