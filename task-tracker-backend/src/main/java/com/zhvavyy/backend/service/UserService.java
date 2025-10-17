package com.zhvavyy.backend.service;

import com.zhvavyy.backend.dto.CurrentUserDto;
import com.zhvavyy.backend.dto.UserUpdateDto;
import com.zhvavyy.backend.dto.UserFilterDto;
import com.zhvavyy.backend.dto.UserReadDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;


public interface UserService {
    CurrentUserDto getUser(UserDetails userDetails);
    Page<UserReadDto> findAllByRole(UserFilterDto filterDto, Pageable pageable);
    UserReadDto update(Long id, UserUpdateDto createDto);
    void delete(Long id);
    List<UserReadDto> getAll();


}
