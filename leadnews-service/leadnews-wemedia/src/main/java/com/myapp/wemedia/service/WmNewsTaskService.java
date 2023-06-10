package com.myapp.wemedia.service;

import java.util.Date;

public interface WmNewsTaskService {

    /**
     * 添加任务到延迟队列中
     * @param newsId 任务id
     * @param publishTime 发布文章的时间/执行任务的时间
     */
    public void addWmNewsToTask(Integer newsId, Date publishTime);

    /**
     * 消费延迟队列中的定时任务：定时发布自媒体文章
     */
    public void processWmNewsInTask();
}
