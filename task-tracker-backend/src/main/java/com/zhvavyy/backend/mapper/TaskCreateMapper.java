package com.zhvavyy.backend.mapper;

import com.zhvavyy.backend.dto.TaskCreateDto;
import com.zhvavyy.backend.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskCreateMapper implements Mapper<TaskCreateDto, Task> {

    @Override
    public Task mapTo(TaskCreateDto object) {
        Task task= new Task();
        copy(object,task);
        return task;
    }

    @Override
    public Task mapTo(TaskCreateDto fromObject, Task toObject) {
         copy(fromObject,toObject);
         return toObject;
    }

    @Override
    public void copy(TaskCreateDto object, Task task){
           task.setTitle(object.getTitle());
           task.setDescription(object.getDescription());
           task.setStatus(object.getStatus());
           task.setDoneAt(object.getDoneAt());
    }
}
