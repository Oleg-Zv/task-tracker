package com.zhvavyy.backend.grpc.mapper;


import com.zhvavyy.backend.dto.TaskDto;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskGrpcMapper {

    public static TaskServiceScheduleProto.TaskDto toProto(TaskDto dto) {
        return TaskServiceScheduleProto.TaskDto.newBuilder()
                .setId(dto.id())
                .setEmail(dto.email())
                .setTitle(dto.title())
                .setStatus(dto.status().name())
                .build();
    }
}