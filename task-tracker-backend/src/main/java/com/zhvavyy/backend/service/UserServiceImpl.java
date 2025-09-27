package com.zhvavyy.backend.service;

import com.zhvavyy.backend.dto.CurrentUserDto;
import com.zhvavyy.backend.dto.UserCreateDto;
import com.zhvavyy.backend.dto.UserFilterDto;
import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.exception.UnauthorizedException;
import com.zhvavyy.backend.exception.UserNotFoundException;
import com.zhvavyy.backend.filter.UserSpecification;
import com.zhvavyy.backend.mapper.CurrentUserMapper;
import com.zhvavyy.backend.mapper.UserCreateMapper;
import com.zhvavyy.backend.mapper.UserMapper;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {


    UserRepository userRepository;
    UserMapper userMapper;
    UserSpecification userSpecification;
    UserCreateMapper userCreateMapper;
    CurrentUserMapper currentUserMapper;


    @Override
    public CurrentUserDto getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(currentUserMapper::mapTo)
                .orElseThrow(()-> new UnauthorizedException("Unauthorize user"));

    }

    @Override
    public Page<UserReadDto> findAllByRole(UserFilterDto filterDto, Pageable pageable) {
       Specification<User>specification= userSpecification.build(filterDto);
       Page<User> users= userRepository.findAll(specification,pageable);
       return users.map(userMapper::mapTo);
    }

    public UserReadDto findById(Long id) {
        return getOrThrow(userRepository.findById(id)
                        .map(userMapper::mapTo),
                "User not found with id: "+id);


    }



    @Transactional
    @Override
    public Optional<UserReadDto> update(Long id, UserCreateDto createDto) {
       return userRepository.findById(id)
               .map(entity-> userCreateMapper.mapTo(createDto,entity))
               .map(userRepository::saveAndFlush)
               .map(userMapper::mapTo);

    }

    @Transactional
    @Override
    public void delete(Long id) {
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("user not fount with id: "+id);
        }
        userRepository.deleteById(id);
    }

    private <T> T getOrThrow(Optional<T> user, String message){
        return user.orElseThrow(()->
                new UserNotFoundException(message));
    }
}
