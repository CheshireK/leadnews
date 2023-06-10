package com.myapp.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.myapp.api.schedule.IScheduleClient;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.constant.TaskTypeEnum;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.schedule.dto.Task;
import com.myapp.model.wemedia.pojo.WmNews;
import com.myapp.util.common.ProtostuffUtil;
import com.myapp.wemedia.service.WmAutoCensorService;
import com.myapp.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class WmNewsTaskServiceImpl implements WmNewsTaskService {

    @Autowired
    private IScheduleClient scheduleClient;

    @Autowired
    private WmAutoCensorService autoCensorService;

    /**
     * 添加任务到延迟队列中
     * @param newsId      任务id
     * @param publishTime 发布文章的时间/执行任务的时间
     */
    @Override
    @Async
    public void addWmNewsToTask(Integer newsId, Date publishTime) {
        if (newsId == null || publishTime == null) {
            return;
        }
        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        WmNews wmNews = new WmNews();
        wmNews.setId(newsId);
        task.setParameters(ProtostuffUtil.serialize(wmNews));
        scheduleClient.addTask(task);
        log.info("添加定时任务：定时发布自媒体文章 id={} publishTime={}", newsId, publishTime);
    }

    /**
     * 消费延迟队列中的定时任务：定时发布自媒体文章
     */
    @Override
    @Scheduled(fixedRate = 1000)
    // @SneakyThrows
    public void processWmNewsInTask() {
        // log.debug("定时任务执行：定时审核、发布自媒体文章");
        ResponseResult responseResult = scheduleClient.poll(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(),
                TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if (responseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode()) &&
                responseResult.getData() != null) {

            Task task = null;
            try {
                String jsonString = JSON.toJSONString(responseResult.getData());
                task = JSON.parseObject(jsonString, Task.class);

                Integer id = getWmNewsId(task);
                autoCensorService.autoCensorWmNews(id);
                log.info("定时任务执行：定时发布、审核文章 id={}", id);
            }
            // 当任务执行失败时再次将任务放入延迟队列中
            // 并将执行时间延后1分钟
            catch (Exception e) {
                if (task!=null){
                    task.setTaskId(null);
                    task.setExecuteTime(task.getExecuteTime() + 1000 * 60);
                    scheduleClient.addTask(task);
                    log.error("定时任务：定时发布、审核文章执行失败, 正在重试 task id={}, 新的执行时间executeTime={}, error message={}",
                            task.getTaskId(), new Date(task.getExecuteTime()), e.getMessage());
                }

            }
        }
    }

    private static Integer getWmNewsId(Task task) {
        byte[] parameters = task.getParameters();
        WmNews wmNews = ProtostuffUtil.deserialize(parameters, WmNews.class);
        return wmNews.getId();
    }
}
