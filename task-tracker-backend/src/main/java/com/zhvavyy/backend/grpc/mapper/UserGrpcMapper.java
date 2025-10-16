package com.zhvavyy.backend.grpc.mapper;

import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserGrpcMapper {

    public static UserServiceScheduleProto.UserDto toProto(UserReadDto userReadDto){
              return   UserServiceScheduleProto.UserDto.newBuilder()
                        .setId(userReadDto.id())
                        .setEmail(userReadDto.email())
                        .setRole(userReadDto.role().getAuthority())
                        .setFirstname(userReadDto.firstname())
                        .setLastname(userReadDto.lastname())
                        .build();
    }
}
