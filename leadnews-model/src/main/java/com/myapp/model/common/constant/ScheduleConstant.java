package com.myapp.model.common.constant;

public class ScheduleConstant {
    /**
     * 初始状态
     */
    public static final Integer SCHEDULED = 0;
    /**
     * 已执行状态
     */
    public static final Integer EXECUTED=1;
    /**
     * 已取消状态
     */
    public static final Integer CANCELLED=2;
    /**
     * 未来数据key前缀
     */
    public static String FUTURE="future_";
    /**
     * 当前数据key前缀
     */
    public static String TOPIC="topic_";

    /**
     * 分布式锁
     */
    public static String LOCK = "FUTURE_TASK_SYNC";
}
