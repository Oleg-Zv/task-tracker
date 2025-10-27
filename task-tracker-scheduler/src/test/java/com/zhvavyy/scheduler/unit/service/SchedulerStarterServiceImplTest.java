package com.zhvavyy.scheduler.unit.service;

import com.zhvavyy.scheduler.service.ReportDispatcherService;
import com.zhvavyy.scheduler.service.SchedulerStarterServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class SchedulerStarterServiceImplTest {

    @InjectMocks
    private SchedulerStarterServiceImpl starterService;
    @Mock
    private ReportDispatcherService dispatcherService;

    @Test
    public void scheduleUserReport(){
        starterService.scheduleUserReport();
        verify(dispatcherService).dispatcherUserReport();
    }
}
