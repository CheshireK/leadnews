package com.myapp.schedule.service;

import com.myapp.model.schedule.dto.Task;

/**
 * 对外访问接口
 */
public interface TaskService {
    /**
     * 添加任务
     * @param task 任务对象
     * @return task id
     */
    public Long addTask(Task task);

    /**
     * 取消任务
     * @param taskId        任务id
     * @return              取消结果
     */
    public boolean cancelTask(long taskId);


    /**
     * 按照类型和优先级来拉取任务
     * @param type 任务类型
     * @param priority 任务优先级
     * @return
     */
    public Task poll(int type,int priority);
}
