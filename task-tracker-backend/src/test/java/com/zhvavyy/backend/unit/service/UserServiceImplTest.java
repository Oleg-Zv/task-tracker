package com.zhvavyy.backend.unit.service;

import com.zhvavyy.backend.dto.CurrentUserDto;
import com.zhvavyy.backend.dto.UserUpdateDto;
import com.zhvavyy.backend.dto.UserFilterDto;
import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.exception.UnauthorizedException;
import com.zhvavyy.backend.exception.UserNotFoundCustomException;
import com.zhvavyy.backend.filter.UserSpecification;
import com.zhvavyy.backend.mapper.CurrentUserMapper;
import com.zhvavyy.backend.mapper.UserCreateMapper;
import com.zhvavyy.backend.mapper.UserMapper;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.repository.UserRepository;
import com.zhvavyy.backend.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static com.zhvavyy.backend.unit.service.data.UserDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("FieldCanBeLocal")
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserMapper currentUserMapper;
    @Mock
    private UserCreateMapper userCreateMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserSpecification userSpecification;
    @Mock
    private UserDetails userDetails;

    private User user;
    private CurrentUserDto currentUserDto;
    private UserFilterDto userFilterRoleUser;
    private UserFilterDto userFilterRoleAdmin;
    private UserReadDto userReadRoleUser;
    private UserReadDto userReadRoleAdmin;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    public void init() {
        user = createUser();
        currentUserDto = getCurrentUserHelper();
        userFilterRoleUser = getUserFilterHelper(Role.USER);
        userFilterRoleAdmin = getUserFilterHelper(Role.ADMIN);
        userReadRoleUser = getUserReadHelper(Role.USER);
        userReadRoleAdmin = getUserReadHelper(Role.ADMIN);
        userUpdateDto = getUserUpdateHelper();

    }

    @Test
    public void getUser() {
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(currentUserMapper.mapTo(user)).thenReturn(currentUserDto);

        CurrentUserDto result = userService.getUser(userDetails);
        assertEquals(currentUserDto.id(), result.id());
        assertEquals(currentUserDto.email(), result.email());
        verify(userRepository).findByEmail(userDetails.getUsername());
    }

    @Test
    public void getUser_shouldThrowExc() {

        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> userService.getUser(userDetails));
        verifyNoMoreInteractions(userRepository);
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    public void shouldFindAllUsersByRoleUsingSpecification(Role role) {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        @SuppressWarnings("unchecked")
        Specification<User> spec = mock(Specification.class);

        when(userSpecification.build(any())).thenReturn(spec);
        when(userRepository.findAll(spec,pageable)).thenReturn(page);

        if (role == Role.USER) {
            when(userMapper.mapTo(user)).thenReturn(userReadRoleUser);

            Page<UserReadDto> users = userService.findAllByRole(userFilterRoleUser, pageable);

            assertEquals(userReadRoleUser.email(), users.getContent().get(0).email());
            assertEquals(userReadRoleUser.role(), users.getContent().get(0).role());
        } else {
            user.setRole(Role.ADMIN);
            when(userMapper.mapTo(user)).thenReturn(userReadRoleAdmin);

            Page<UserReadDto> users = userService.findAllByRole(userFilterRoleAdmin, pageable);

            assertEquals(userReadRoleAdmin.email(), users.getContent().get(0).email());
            assertEquals(userReadRoleAdmin.role(), users.getContent().get(0).role());

        }
        verify(userSpecification).build(any());
        verify(userRepository).findAll(spec, pageable);
    }

    @Test
    public void findById() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.mapTo(user)).thenReturn(userReadRoleUser);

        UserReadDto maybeUser = userService.findById(USER_ID);
        assertEquals(userReadRoleUser.email(), maybeUser.email());
        assertEquals(user.getId(), maybeUser.id());

        verify(userRepository).findById(USER_ID);
    }

    @Test
    public void findById_shouldThrowExc() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundCustomException.class, () -> userService.findById(USER_ID));
    }

    @Test
    public void updateUser() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userCreateMapper.mapTo(userUpdateDto, user)).thenReturn(user);
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(userMapper.mapTo(user)).thenReturn(userReadRoleUser);

        UserReadDto updateMaybe = userService.update(USER_ID, userUpdateDto);
        assertEquals(userReadRoleUser.email(), updateMaybe.email());
        assertEquals(userReadRoleUser.id(), updateMaybe.id());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    public void deleteUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.delete(USER_ID);
        verify(userRepository).delete(user);
    }

    @Test
    public void deleteUser_shouldThrowExc() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundCustomException.class, () -> userService.delete(USER_ID));
    }

    @Test
    public void getAllUsers() {

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.mapTo(user)).thenReturn(userReadRoleUser);

        List<UserReadDto> all = userService.getAll();
        assertFalse(all.isEmpty());
        verify(userRepository).findAll();
        verify(userMapper).mapTo(user);
    }

    @Test
    public void getAllUsers_shouldEmpty() {

        when(userRepository.findAll()).thenReturn(List.of());
        List<UserReadDto> all = userService.getAll();
        assertTrue(all.isEmpty());
    }
}
