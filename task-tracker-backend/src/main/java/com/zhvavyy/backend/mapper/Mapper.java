package com.zhvavyy.backend.mapper;

public interface Mapper<F, T> {
T mapTo(F object);

default T mapTo(F fromObject, T toObject){
    return toObject;}

default void copy(F fromObject, T toObject){}
}
