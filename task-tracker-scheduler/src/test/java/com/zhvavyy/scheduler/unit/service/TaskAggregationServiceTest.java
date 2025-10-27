package com.zhvavyy.scheduler.unit.service;


import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import com.zhvavyy.scheduler.dto.Statuses;
import com.zhvavyy.scheduler.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.scheduler.service.GrpcTaskClientService;
import com.zhvavyy.scheduler.service.TaskAggregationService;
import com.zhvavyy.scheduler.service.UserReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("FieldCanBeLocal")
@ExtendWith(MockitoExtension.class)
public class TaskAggregationServiceTest {

    @InjectMocks
    private TaskAggregationService aggregationService;
    @Mock
    private GrpcTaskClientService taskClientService;
    @Mock
    private UserReportService userReportService;
    private MessageForEmail message;
    private List<Long> usersId;
    private TaskServiceScheduleProto.TaskDto taskDto;
    private Map<Long, List<TaskServiceScheduleProto.TaskDto>> tasksUserMap;
    private TaskServiceScheduleProto.TaskDto done;
    private TaskServiceScheduleProto.TaskDto pending;

    private final String STATUS_DONE = "DONE";
    private final String STATUS_PENDING = "PENDING";
    private final Integer USERS_ID_SIZE = 3;
    private final String TEST_TITLE_DONE = "refactor code";
    private final String TEST_TITLE_PENDING = "read code";
    private final String EMAIL = "user@gmail.com";
    private final Long USER_ID = 1L;

    @BeforeEach
    public void init() {
        message = new MessageForEmail();
        message.setRecipient(EMAIL);
        message.setMsgBody("test");
        message.setSubject("test");


        usersId = Arrays.asList(1L, 2L, 3L);
        taskDto = TaskServiceScheduleProto.TaskDto.newBuilder()
                .setEmail(EMAIL)
                .setStatus(STATUS_DONE)
                .build();

        tasksUserMap = Map.of(1L, List.of(TaskServiceScheduleProto.TaskDto.newBuilder()
                        .setStatus(STATUS_DONE).build()),
                2L, List.of(TaskServiceScheduleProto.TaskDto.newBuilder()
                        .setStatus(STATUS_PENDING).build())
        );
        done = TaskServiceScheduleProto.TaskDto.newBuilder()
                .setId(USER_ID)
                .setTitle(TEST_TITLE_DONE)
                .setEmail(EMAIL)
                .setStatus(STATUS_DONE)
                .build();

        pending = TaskServiceScheduleProto.TaskDto.newBuilder()
                .setId(USER_ID)
                .setTitle(TEST_TITLE_PENDING)
                .setEmail(EMAIL)
                .setStatus(STATUS_PENDING)
                .build();
    }

    @Test
    public void buildUserTasksReport_shouldReturnExpectedResult() {
        List<Long> userIds = List.of(USER_ID);
        Map<Long, List<TaskServiceScheduleProto.TaskDto>> mockTasks = Map.of(
                USER_ID, List.of(
                        TaskServiceScheduleProto.TaskDto.newBuilder()
                                .setEmail(EMAIL)
                                .setStatus(STATUS_DONE)
                                .build()
                )
        );
        Map<Long, Map<String, Integer>> mockStats = Map.of(
                USER_ID, Map.of(STATUS_DONE, 1, STATUS_PENDING, 0)
        );
        Map<Long, Statuses> mockReports = Map.of(
                USER_ID, new Statuses("done", "", "")
        );
        List<MessageForEmail> mockMessages = List.of(
                MessageForEmail.builder()
                        .recipient(EMAIL)
                        .msgBody("done")
                        .subject("Выполненных задач за сегодня: 1")
                        .build()
        );

        TaskAggregationService spyService = spy(aggregationService);

        when(userReportService.getUsersId()).thenReturn(userIds);
        when(taskClientService.getResponseTask(USER_ID))
                .thenReturn(mockTasks.get(USER_ID));
        doReturn(mockStats).when(spyService).countStatuses(any());
        doReturn(mockReports).when(spyService).formingReports(any());
        doReturn(mockMessages).when(spyService).formingDto(any(), any(), any());

        List<MessageForEmail> result = spyService.buildUserTasksReport();

        assertEquals(1, result.size());
        MessageForEmail msg = result.get(0);
        assertEquals(EMAIL, msg.getRecipient());
        assertTrue(msg.getSubject().contains("Выполненных задач"));

        verify(userReportService).getUsersId();
        verify(taskClientService).getResponseTask(USER_ID);
        verify(spyService).countStatuses(any());
        verify(spyService).formingReports(any());
        verify(spyService).formingDto(any(), any(), any());
    }

    @Test
    public void getUserTasks() {
        when(taskClientService.getResponseTask(usersId.get(0))).thenReturn(List.of(taskDto));
        Map<Long, List<TaskServiceScheduleProto.TaskDto>> result = aggregationService.getUserTasks(usersId);

        assertEquals(usersId.size(), result.size());
        verify(taskClientService, times(USERS_ID_SIZE)).getResponseTask(anyLong());
    }

    @Test
    public void shouldCountDoneAndPendingTasksCorrectly() {
        Map<Long, Map<String, Integer>> result = aggregationService.countStatuses(tasksUserMap);
        long actualCountDone = result.values().stream()
                .filter(s -> s.getOrDefault(STATUS_DONE, 0) > 0)
                .count();
        long actualCountPending = result.values().stream()
                .filter(s -> s.getOrDefault(STATUS_PENDING, 0) > 0)
                .count();

        assertEquals(1, actualCountDone);
        assertEquals(1, actualCountPending);
    }

    @Test
    public void formingReports_forMsgBody_InMessageForEmail() {
        Map<Long, List<TaskServiceScheduleProto.TaskDto>> map = new HashMap<>();
        map.put(1L, List.of(done, pending));

        Map<Long, Statuses> result = aggregationService.formingReports(map);
        assertEquals(1, result.size());
        Statuses statuses = result.get(1L);
        assertTrue(statuses.doneBody().contains(TEST_TITLE_DONE));
        assertTrue(statuses.pendingBody().contains(TEST_TITLE_PENDING));
        assertTrue(statuses.combinedBody().contains("Выполненные задачи:"));
        assertTrue(statuses.combinedBody().contains("Невыполненные задачи:"));
    }

    @Test
    public void formingDto_shouldBuildEmailFor_DoneTasks() {
        Map<Long, List<TaskServiceScheduleProto.TaskDto>> tasksUserMap = new HashMap<>();
        tasksUserMap.put(USER_ID, List.of(done));
        int doneCount= 1;

        Map<Long, Map<String, Integer>> statusesMap = new HashMap<>();
        statusesMap.put(USER_ID, Map.of(STATUS_DONE, doneCount));

        Map<Long, Statuses> reportMap = new HashMap<>();
        reportMap.put(USER_ID, new Statuses(
                "- "+TEST_TITLE_DONE+"\n",
                null,
                "Выполненные задачи:\n- "+ TEST_TITLE_DONE

        ));
        List<MessageForEmail> result = aggregationService.formingDto(reportMap, statusesMap, tasksUserMap);
        assertEquals(1, result.size());
        MessageForEmail msg = result.get(0);
        assertEquals("- "+TEST_TITLE_DONE+"\n", msg.getMsgBody());
        assertEquals(EMAIL, msg.getRecipient());
        assertTrue(msg.getSubject().contains("Выполненных задач за сегодня: "));
    }

    @Test
    public void formingDto_shouldBuildEmailFor_PendingTasks() {
        Map<Long, List<TaskServiceScheduleProto.TaskDto>> tasksUserMap = new HashMap<>();
        tasksUserMap.put(USER_ID, List.of(pending));
        int pendingCount= 1;

        Map<Long, Map<String, Integer>> statusesMap = new HashMap<>();
        statusesMap.put(USER_ID, Map.of(STATUS_PENDING, pendingCount));

        Map<Long, Statuses> reportMap = new HashMap<>();
        reportMap.put(USER_ID, new Statuses(
                null,
                "- "+TEST_TITLE_PENDING+"\n",
                "\nНевыполненные задачи:\n"+ TEST_TITLE_PENDING

        ));
        List<MessageForEmail> result = aggregationService.formingDto(reportMap, statusesMap, tasksUserMap);
        assertEquals(1, result.size());
        MessageForEmail msg = result.get(0);
        assertEquals("- "+TEST_TITLE_PENDING+"\n", msg.getMsgBody());
        assertEquals(EMAIL, msg.getRecipient());
        assertTrue(msg.getSubject().contains("Кол-во несделанных задач: "));
    }

    @Test
    public void formingDto_shouldBuildEmailFor_CombinedTasks() {
        Map<Long, List<TaskServiceScheduleProto.TaskDto>> tasksUserMap = new HashMap<>();
        tasksUserMap.put(USER_ID, List.of(done,pending));
        int doneCount=1;
        int pendingCount = 1;

        Map<Long, Map<String, Integer>> statusesMap = new HashMap<>();
        statusesMap.put(USER_ID, Map.of(STATUS_DONE, doneCount, STATUS_PENDING, pendingCount));

        Map<Long, Statuses> reportMap = new HashMap<>();
        reportMap.put(USER_ID, new Statuses(
                "- " + TEST_TITLE_DONE + "\n",
                "- " + TEST_TITLE_PENDING + "\n",
                "Выполненные задачи:\n- " + TEST_TITLE_DONE + "\n" +
                        "\nНевыполненные задачи:\n" + TEST_TITLE_PENDING

        ));
        List<MessageForEmail> result = aggregationService.formingDto(reportMap, statusesMap, tasksUserMap);
        assertEquals(1, result.size());
        MessageForEmail msg = result.get(0);
        assertEquals("Выполненные задачи:\n- " + TEST_TITLE_DONE + "\n" +
                        "\nНевыполненные задачи:\n" + TEST_TITLE_PENDING,
                msg.getMsgBody());

        assertEquals(EMAIL, msg.getRecipient());
        assertTrue(msg.getSubject().contains("Итоги дня: \n" +
                "Задач выполнено: " + doneCount +
                "\nНесделанных задач: " + pendingCount));
    }

}
