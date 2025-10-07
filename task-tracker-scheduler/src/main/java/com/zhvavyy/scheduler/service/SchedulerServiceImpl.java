package com.zhvavyy.scheduler.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Scheduled(cron = "${task.cron.expression}" )
    @Override
    public void scheduleUserReport() {

    }
}
