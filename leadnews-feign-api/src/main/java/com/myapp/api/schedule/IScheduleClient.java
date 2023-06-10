package com.myapp.api.schedule;

import com.myapp.api.schedule.fallback.IScheduleClientFallback;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.schedule.dto.Task;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "leadnews-schedule",fallbackFactory = IScheduleClientFallback.class)
public interface IScheduleClient {
    /**
     * 添加任务
     * @param task   任务对象
     * @return       任务id
     */
    @PostMapping("/api/v1/task/add")
    public ResponseResult addTask(@RequestBody Task task);

    /**
     * 取消任务
     * @param taskId        任务id
     * @return              取消结果
     */
    @GetMapping("/api/v1/task/cancel/{taskId}")
    public ResponseResult cancelTask(@PathVariable("taskId") long taskId);

    /**
     * 按照类型和优先级来拉取任务
     * @param type
     * @param priority
     * @return
     */
    @GetMapping("/api/v1/task/poll")
    public ResponseResult poll(@RequestParam("type") Integer type, @RequestParam("priority") Integer priority);
}
