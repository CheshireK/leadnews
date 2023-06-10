package com.myapp.schedule.feign;

import com.myapp.api.schedule.IScheduleClient;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.schedule.dto.Task;
import com.myapp.schedule.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ScheduleClient implements IScheduleClient {

    @Autowired
    private TaskService taskService;

    /**
     * 添加任务
     * @param task 任务对象
     * @return 任务id
     */
    @Override
    @PostMapping("/api/v1/task/add")
    public ResponseResult addTask(@RequestBody Task task) {
        return ResponseResult.okResult(taskService.addTask(task));
    }

    /**
     * 取消任务
     * @param taskId 任务id
     * @return 取消结果
     */
    @GetMapping("/api/v1/task/cancel/{taskId}")
    public ResponseResult cancelTask(@PathVariable("taskId") long taskId) {
        return ResponseResult.okResult(taskService.cancelTask(taskId));
    }

    /**
     * 按照类型和优先级来拉取任务
     * @param type 任务类型
     * @param priority 任务优先级
     * @return 任务对象
     */
    @GetMapping("/api/v1/task/poll")
    public ResponseResult poll(@RequestParam("type") Integer type, @RequestParam("priority") Integer priority) {
        return ResponseResult.okResult(taskService.poll(type, priority));
    }
}
