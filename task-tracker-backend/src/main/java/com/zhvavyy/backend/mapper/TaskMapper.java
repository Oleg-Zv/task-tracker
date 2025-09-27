package com.zhvavyy.backend.mapper;

import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper implements Mapper<Task, TaskReadDto> {


    @Override
    public TaskReadDto mapTo(Task object) {
        return  new TaskReadDto(
                object.getId(),
                object.getTitle(),
                object.getStatus(),
                object.getUser().getEmail(),
                object.getCreatedAt(),
                object.getDoneAt()
        );
    }
}
