package com.zhvavyy.backend.mapper;

import com.zhvavyy.backend.dto.TaskResponse;
import com.zhvavyy.backend.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskResponseMapper implements Mapper<Task, TaskResponse> {

    @Override
    public TaskResponse mapTo(Task object) {
        return new TaskResponse(
                object.getId(),
                object.getTitle(),
                object.getStatus()
        );
    }
}
