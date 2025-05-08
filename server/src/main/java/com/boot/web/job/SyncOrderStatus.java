package com.boot.web.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SyncOrderStatus {

    // 每隔一分钟执行一次
//    @Scheduled(cron = "0 * * * * *")
    public void myTask() {

    }

}
