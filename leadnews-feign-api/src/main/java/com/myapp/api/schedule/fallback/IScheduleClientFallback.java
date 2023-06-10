package com.myapp.api.schedule.fallback;

import com.myapp.api.schedule.IScheduleClient;
import com.myapp.model.common.constant.AppHttpCodeEnum;
import com.myapp.model.common.dto.ResponseResult;
import com.myapp.model.schedule.dto.Task;
import lombok.extern.slf4j.Slf4j;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class IScheduleClientFallback implements FallbackFactory<IScheduleClient> {
    /**
     * Returns an instance of the fallback appropriate for the given cause.
     * @param cause cause of an exception.
     * @return fallback
     */
    @Override
    public IScheduleClient create(Throwable cause) {
        return new IScheduleClient() {
            @Override
            public ResponseResult addTask(Task task) {
                log.error("IScheduleClient-addTask fallback, taskId={}, error message={}",task.getTaskId(), cause.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }

            @Override
            public ResponseResult cancelTask(long taskId) {
                log.error("IScheduleClient-cancelTask fallback, taskId={}, error message={}",
                        taskId, cause.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }

            @Override
            public ResponseResult poll(Integer type, Integer priority) {
                log.error("IScheduleClient-poll fallback task type={}, priority={}  error message={}",
                        type, priority, cause.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }
        };
    }
}
