package com.myapp.xxljob.demo.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HelloJob {

    @XxlJob(value = "demoJobHandler")
    public void Hello(){

        log.info("执行简单任务");
    }

    // 示例数据
    String[] dbs ={"db1","db2","db3","db4"};
    String[] tables ={"t1","t2","t3"};

    @XxlJob(value = "s")
    public void shardingTask(){
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.info("shardIndex={}, shardTotal={}", shardIndex, shardTotal);
        for (String db : dbs) {
            for (String table : tables) {
                if (table.hashCode()%shardTotal == shardIndex){
                    log.info("处理数据库{}，中的{}表",db, table);
                }
            }
        }
    }
}
