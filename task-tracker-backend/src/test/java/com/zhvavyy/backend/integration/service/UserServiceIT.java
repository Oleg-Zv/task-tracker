package com.zhvavyy.backend.integration.service;

import com.zhvavyy.backend.dto.*;
import com.zhvavyy.backend.exception.UnauthorizedException;
import com.zhvavyy.backend.exception.UserNotFoundCustomException;
import com.zhvavyy.backend.integration.BaseIntegrationTest;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.repository.UserRepository;
import com.zhvavyy.backend.service.UserService;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureMockMvc
@DisplayName("Integration test for UserService")
public class UserServiceIT extends BaseIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDetails userDetails;
    private UserFilterDto filterDto;
    private UserReadDto userReadDto;
    private UserUpdateDto updateDto;

    private User existingUser;
    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_FIRSTNAME = "Hero";
    private static final String TEST_LASTNAME = "Heros";
    private static final String TEST_PASSWORD = "temp123";

    private static final String UPDATE_FNAME = "update name";
    private static final String UPDATE_LNAME = "update lname";
    private static final String UPDATE_PASSWORD = "changePass";
    private static final String UPDATE_EMAIL = "myNewSecret";

    private static final Long ID_INVALID = -1L;


    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        existingUser = userRepository.save(User.builder()
                .email(TEST_EMAIL)
                .role(Role.USER)
                .firstname(TEST_FIRSTNAME)
                .lastname(TEST_LASTNAME)
                .password(TEST_PASSWORD)
                .build());

        userDetails = new CustomUserDetails(existingUser);
        filterDto = new UserFilterDto(TEST_EMAIL, Role.USER);
        userReadDto = new UserReadDto(existingUser.getId(), TEST_EMAIL, Role.USER, TEST_FIRSTNAME, TEST_LASTNAME);
        updateDto = new UserUpdateDto(UPDATE_FNAME, UPDATE_LNAME, UPDATE_PASSWORD, UPDATE_EMAIL, Role.ADMIN);
    }

    @Test
    @DisplayName("currentUser(): Should return current user by userDetails")
    public void shouldReturnCurrentUser_whenUserDetailsCorrect() {
        CurrentUserDto result = userService.getUser(userDetails);
        assertAll(
                () -> assertEquals(existingUser.getEmail(), result.email()),
                () -> assertEquals(Role.USER, result.role())
        );
    }

    @Test
    @DisplayName("currentUser(): should return exception, when: userDetails invalid")
    public void shouldExceptionCurrentUser_whenUserDetailsInvalid() {
        CustomUserDetails fakeUserDetails = new CustomUserDetails(new User());
        assertThrows(UnauthorizedException.class, () -> userService.getUser(fakeUserDetails));
    }

    @Test
    @DisplayName("shouldAllUsersByRole(): should return all users by role")
    public void shouldAllUsersByRole_whenRoleUser() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<UserReadDto> allByRole = userService.findAllByRole(filterDto, pageable);

        UserReadDto result = allByRole.getContent().stream()
                .filter(u -> u.email().equals(TEST_EMAIL))
                .findFirst()
                .orElseThrow();
        assertAll(
                () -> assertNotNull(allByRole),
                () -> assertEquals(userReadDto.email(), result.email()),
                () -> assertEquals(userReadDto.role(), result.role()),
                () -> assertEquals(userReadDto.firstname(), result.firstname())
        );
    }
    @Test
    @DisplayName("shouldAllUsersByRole(): should return all users by role")
    public void shouldAllUsersByRole_whenRoleAdmin() {

        PageRequest pageable = PageRequest.of(0, 10);
        User another = userRepository.save(User.builder()
                .email("admin@mail.com")
                .role(Role.ADMIN)
                .firstname("Aloe")
                .lastname("Blue")
                .password("xss222")
                .build());
        UserFilterDto filterByAdmin = new UserFilterDto(another.getEmail(), Role.ADMIN);
        Page<UserReadDto> page = userService.findAllByRole(filterByAdmin, pageable);

        assertAll(
                () -> assertTrue(page.stream()
                        .allMatch(u -> u.role() == Role.ADMIN)),
                ()->assertEquals(1, page.getTotalElements())
        );
    }

    @Test
    @DisplayName("shouldReturnUserById(): should return user by ID")
    public void shouldReturnUserById_whenIdCorrect() {

        UserReadDto result = userService.findById(existingUser.getId());
        assertAll(
                () -> assertEquals(userReadDto.email(), result.email()),
                () -> assertEquals(userReadDto.firstname(), result.firstname()),
                () -> assertEquals(userReadDto.lastname(), result.lastname()),
                () -> assertEquals(userReadDto.role(), result.role())

        );
    }

    @Test
    @DisplayName("shouldReturnUserById(): should return exception, user ID invalid")
    public void shouldReturnUserByIdEmpty_whenIdNotFound() {
        assertThrows(UserNotFoundCustomException.class, () -> userService.findById(ID_INVALID));
    }

    @Test
    @DisplayName("update(): should return update user")
    public void shouldReturnUpdateUser_whenUserIdCorrect() {

        UserReadDto result = userService.update(existingUser.getId(), updateDto);
        User actualUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(UPDATE_EMAIL, actualUser.getEmail()),
                () -> assertEquals(Role.ADMIN, actualUser.getRole()),
                () -> assertTrue(passwordEncoder.matches(UPDATE_PASSWORD, actualUser.getPassword())),
                ()->assertNotEquals(UPDATE_PASSWORD, actualUser.getPassword())

        );

    }

    @Test
    @DisplayName("update(): should return exception, user ID invalid")
    public void shouldReturnExceptionUpdateUser_whenUserIdNotFound() {
        assertThrows(UserNotFoundCustomException.class, () -> userService.update(ID_INVALID, updateDto));
    }

    @Test
    @DisplayName("delete(): should delete existing user by ID")
    public void deleteById_shouldDeleteExistingUser() {
        Optional<User> exist = userRepository.findById(existingUser.getId());

        userService.delete(existingUser.getId());
        Optional<User> deleted = userRepository.findById(existingUser.getId());
        assertAll(
                () -> assertTrue(exist.isPresent()),
                () -> assertTrue(deleted.isEmpty())
        );
    }

    @Test
    @DisplayName("delete(): should throw exception when deleting non-existing user")
    public void deleteById_shouldThrowException_whenNotFound() {
        assertThrows(UserNotFoundCustomException.class, () -> userService.delete(ID_INVALID));
    }

    @Test
    @DisplayName("shouldReturnAllUsers(): Should return all existing users")
    public void shouldReturnAllUsers() {
        List<UserReadDto> all = userService.getAll();

        UserReadDto result = all.stream()
                .filter(t -> t.email().equals(TEST_EMAIL))
                .findFirst()
                .orElseThrow();

        assertAll(
                () -> assertEquals(userReadDto.email(), all.get(0).email()),
                () -> assertEquals(Role.USER, result.role()),
                () -> assertEquals(TEST_EMAIL, result.email()),
                ()->assertEquals(1, all.size())
        );
    }

    @Test
    @DisplayName("getAllUsers(): Should return empty page when no users exist")
    public void getAllUsers_whenNoUsers() {
        userRepository.deleteAll();

        List<UserReadDto> shouldNoUsers = userService.getAll();
        assertNotNull(shouldNoUsers);
        assertTrue(shouldNoUsers.isEmpty());
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }
}
