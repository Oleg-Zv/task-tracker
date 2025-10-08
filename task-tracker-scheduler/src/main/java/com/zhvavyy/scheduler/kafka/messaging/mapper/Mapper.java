package com.zhvavyy.scheduler.kafka.messaging.mapper;

public interface Mapper<F, T> {
    T mapTo(F object);
}
