package com.zhvavyy.backend.unit.mapper;

import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.mapper.TaskMapper;
import com.zhvavyy.backend.model.Task;
import com.zhvavyy.backend.unit.service.data.TaskDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskMapperTest {

    @InjectMocks
    private TaskMapper taskMapper;

    private Task task;

    @BeforeEach
    public void init(){
        task= TaskDataFactory.createTask();
    }

    @Test
    public void mapTo(){
        TaskReadDto result = taskMapper.mapTo(task);
        assertEquals(task.getUser().getEmail(), result.email());
        assertEquals(task.getTitle(), result.title());
    }
}
