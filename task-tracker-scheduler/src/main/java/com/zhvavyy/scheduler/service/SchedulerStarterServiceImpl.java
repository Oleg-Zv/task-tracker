package com.zhvavyy.scheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerStarterServiceImpl implements SchedulerService {


    private final ReportDispatcherService report;

    @Scheduled(cron = "${task.cron.expression}" )
    @Override
    public void scheduleUserReport() {
      report.dispatcherUserReport();
    }
}
