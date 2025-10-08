package com.zhvavyy.scheduler.kafka.messaging.mapper;

import com.my.grpc.user.UserService;
import com.zhvavyy.scheduler.kafka.messaging.dto.MessageForEmail;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListDtoMapper implements Mapper<List<UserService.UserDto>, List<MessageForEmail>> {

    @Override
    public List<MessageForEmail> mapTo(List<UserService.UserDto> object) {
        return object.stream().map(user->
           MessageForEmail.builder()
                    .recipient(user.getEmail())
                    .msgBody("Отчет")
                    .subject("Ваша активность ")
                    .build()).toList();
    }
}
