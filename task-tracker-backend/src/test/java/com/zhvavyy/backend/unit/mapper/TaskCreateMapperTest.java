package com.zhvavyy.backend.unit.mapper;

import com.zhvavyy.backend.dto.TaskCreateDto;
import com.zhvavyy.backend.mapper.TaskCreateMapper;
import com.zhvavyy.backend.model.Task;
import com.zhvavyy.backend.model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.zhvavyy.backend.unit.service.data.TaskDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskCreateMapperTest {


    @InjectMocks
    private TaskCreateMapper createMapper;

    private Task task;
    private TaskCreateDto createDto;

    @BeforeEach
    public void init(){
        task = createTask();
        createDto = new TaskCreateDto(TEST_TITLE,TEST_DESC,Status.PENDING);
    }

    @Test
    public void mapTo() {
        Task task1 = createMapper.mapTo(createDto);
        assertEquals(task.getTitle(), task1.getTitle());
        assertEquals(task.getStatus(), task1.getStatus());
    }

    @Test
    public void mapToWithTwoArguments() {
        Task task1 = createMapper.mapTo(createDto, task);
        assertEquals(task.getTitle(), task1.getTitle());
        assertEquals(task.getStatus(), task1.getStatus());
    }
    
}
