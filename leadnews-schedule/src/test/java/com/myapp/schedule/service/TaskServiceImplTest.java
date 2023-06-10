package com.myapp.schedule.service;

import com.myapp.model.schedule.dto.Task;
import com.myapp.schedule.ScheduleApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@SpringBootTest(classes = ScheduleApplication.class)
public class TaskServiceImplTest {

    @Autowired
    TaskService taskService;

    @Test
    public void addTask() {

        for (int i = 0; i < 10; i++) {
            Task task =  new Task();
            task.setExecuteTime(new Date().getTime());
            task.setTaskType(100);
            task.setPriority(50);
            task.setParameters("task test".getBytes());
            long time = LocalDateTime.now().plusMinutes(i).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            task.setExecuteTime(time);
            taskService.addTask(task);
        }

    }

    @Test
    public void cancelTask() {
        boolean cancelTask = taskService.cancelTask(1665209492112961537L);
        System.out.println(cancelTask);
    }

    @Test
    public void poll() {
        Task poll = taskService.poll(100, 50);
        // poll.getParameters()
    }
}