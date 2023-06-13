package com.myapp.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myapp.common.redis.CacheService;
import com.myapp.model.schedule.ScheduleConstant;
import com.myapp.model.schedule.dto.Task;
import com.myapp.model.schedule.pojo.Taskinfo;
import com.myapp.model.schedule.pojo.TaskinfoLogs;
import com.myapp.schedule.mapper.TaskinfoLogsMapper;
import com.myapp.schedule.mapper.TaskinfoMapper;
import com.myapp.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskinfoMapper taskinfoMapper;

    @Autowired
    private TaskinfoLogsMapper logsMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * 添加任务
     *
     * @param task 任务对象
     * @return task id
     */
    @Override
    public Long addTask(Task task) {
        boolean result = saveTaskToDb(task);
        // 保存到redis中
        if (result) {
            addTaskToCache(task);
        }
        return task.getTaskId();
    }

    /**
     * 取消任务
     *
     * @param taskId 任务id
     * @return 取消结果
     */
    @Override
    public boolean cancelTask(long taskId) {
        boolean flag = false;
        // 删除任务，更新日志
        Task task = updateTaskinfoLogs(taskId, ScheduleConstant.CANCELLED);
        if (task != null) {
            removeTaskFromCache(task);
            flag = true;
        }
        return flag;
    }



    /**
     * 按照类型和优先级来拉取任务
     * @param type     任务类型
     * @param priority 任务优先级
     * @return 任务对象
     */
    @Override
    public Task poll(int type, int priority) {
        Task task = null;
        try {
            // String key = generateTopicKey(type, priority);
            String taskJson = cacheService.lRightPop(generateTopicKey(type, priority));
            if (StringUtils.hasLength(taskJson)){
                task = JSON.parseObject(taskJson, Task.class);
                updateTaskinfoLogs(task.getTaskId(), ScheduleConstant.EXECUTED);
            }
        } catch (Exception e) {
            log.error("任务获取出现异常, message={}", e.getMessage());
        }

        return task;
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 每分钟执行
     * 将zset的数据添加到list中
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh(){
        // 获取所有未来任务的key
        Set<String> futureKeys = cacheService.scan(ScheduleConstant.FUTURE + "*");
        // 加锁
        String lockToken = cacheService.tryLock(ScheduleConstant.LOCK, 30, TimeUnit.SECONDS);
        if (StringUtils.isEmpty(lockToken)){
            return;
        }
        // Set<String> tasks = null;
        for (String futureKey : futureKeys) {
            // futureKey: future_100_50 -> topic_100_50
            String topicKey = futureKey.replace(ScheduleConstant.FUTURE, ScheduleConstant.TOPIC);
            // 获取截止到当前时间的所有task
            // Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
            Optional.ofNullable(cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis()))
                    .filter(tasks->!CollectionUtils.isEmpty(tasks))
                    .ifPresent(tasks -> {
                        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                            StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                            String[] array = tasks.toArray(new String[0]);
                            stringRedisConnection.rPush(topicKey, array);
                            stringRedisConnection.zRem(futureKey, array);
                            return null;
                        });
                        log.debug("将zset中的key='{}'的task添加到了list",futureKey);
                    });
        }
        log.debug("定时任务-将zset的Task添加到list中");
        // 解锁
        cacheService.unlock(ScheduleConstant.LOCK, lockToken);
    }

    /**
     * 定时任务：每五分钟执行一次，将数据库中的数据同步到redis中
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @PostConstruct
    public void syncDbAndCache(){
        Date nextScheduleTime = getNextScheduleTime();
        LambdaQueryWrapper<Taskinfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(Taskinfo::getExecuteTime, nextScheduleTime);

        Optional.ofNullable(taskinfoMapper.selectList(queryWrapper))
                .ifPresent(taskinfoList->{
                    // 将数据库task
                    for (Taskinfo taskinfo : taskinfoList) {
                        Task task = new Task();
                        BeanUtils.copyProperties(taskinfo, task);
                        task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                        addTaskToCache(task);
                    }
                    // 删除数据库数据
                    List<Long> ids = taskinfoList.stream()
                            .map(Taskinfo::getTaskId)
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(ids)){
                        taskinfoMapper.deleteBatchIds(ids);
                    }

                });
    }


    /**
     * 删除cache中的任务
     * @param task 任务
     */
    private void removeTaskFromCache(Task task) {
        // String key = generateTopicKey(task);

        if (task.getExecuteTime() <= System.currentTimeMillis()){
            cacheService.lRemove(generateTopicKey(task), 0, JSON.toJSONString(task));
            log.debug("从cache list中删除任务, task id={}", task.getTaskId());
        }
        else {
            cacheService.zRemove(generateFutureKey(task), JSON.toJSONString(task));
            log.debug("从cache zset中删除任务, task id={}", task.getTaskId());
        }

    }

    /**
     * 删除任务，更新任务日志状态
     * @param taskId 任务id
     * @param status 任务状态
     * @return 任务
     */
    private Task updateTaskinfoLogs(long taskId, Integer status) {
        Task task = null;
        try {
            taskinfoMapper.deleteById(taskId);
            log.debug("从数据库删除任务, task id={}", taskId);

            TaskinfoLogs taskinfoLogs = logsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            logsMapper.updateById(taskinfoLogs);
            log.debug("更新任务日志, task id={}", taskId);

            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs, task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (BeansException e) {
            log.error("任务取消出现异常, task id={}, message={}", taskId, e.getMessage());
        }

        return task;
    }

    private void addTaskToCache(Task task) {
        // String key = generateTopicKey(task);

        // 获取5分钟后的毫秒值
        long nextScheduleTime = getNextScheduleTime().getTime();

        // 如果任务的执行时间小于等于当前时间，存入list
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(generateTopicKey(task), JSON.toJSONString(task));
            log.debug("任务保存至Cache List, task id={}", task.getTaskId());
        }
        // 如果任务的执行时间大于当前时间 而且小于等于预设时间
        else if (task.getExecuteTime() <= nextScheduleTime) {
            cacheService.zAdd(generateFutureKey(task),
                    JSON.toJSONString(task), task.getExecuteTime());
            log.debug("任务保存至Cache zset, task id={}", task.getTaskId());
        }

    }

    private static Date getNextScheduleTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        return calendar.getTime();
    }

    private static String generateTopicKey(Task task) {
        return generateTopicKey(task.getTaskType(), task.getPriority());
    }

    private static String generateTopicKey(int type, int priority) {
        return ScheduleConstant.TOPIC  + type + "_" + priority;
    }

    private static String generateFutureKey(Task task) {
        return generateFutureKey(task.getTaskType(), task.getPriority());
    }

    private static String generateFutureKey(int type, int priority) {
        return ScheduleConstant.FUTURE  + type + "_" + priority;
    }
    private boolean saveTaskToDb(Task task) {
        boolean flag = false;
        try {
            // 保存任务表
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task, taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            int taskinfoResult = taskinfoMapper.insert(taskinfo);

            Long taskId = taskinfo.getTaskId();
            task.setTaskId(taskId);
            // 保存任务日志表
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo, taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstant.SCHEDULED);
            int taskinfoLogsResult = logsMapper.insert(taskinfoLogs);

            if (taskinfoResult == 1 && taskinfoLogsResult == 1) {
                flag = true;
            }
            log.debug("任务保存至数据库, task id={}", task.getTaskId());
        } catch (Exception e) {
            log.error("任务保存至数据库出现问题, message={}", e.getMessage());
        }
        return flag;
    }
}
