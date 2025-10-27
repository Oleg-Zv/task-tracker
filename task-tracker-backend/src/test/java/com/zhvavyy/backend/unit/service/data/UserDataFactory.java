package com.zhvavyy.backend.unit.service.data;

import com.zhvavyy.backend.dto.CurrentUserDto;
import com.zhvavyy.backend.dto.UserFilterDto;
import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.dto.UserUpdateDto;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;


public final class UserDataFactory {

    private UserDataFactory(){}

    public static final Long USER_ID = 2L;
    public static final String TEST_EMAIL = "test@gmail.com";
    public static final String TEST_FIRSTNAME = "test user";
    public static final String TEST_LASTNAME = "test user";
    public static final String TEST_RAW_PASSWORD = "secret";

    private static final String TEST_UPDATE_EMAIL = "new user";
    private static final String TEST_UPDATE_FNAME = "new user";
    private static final String TEST_UPDATE_LNAME = "new user";
    private static final String TEST_UPDATE_RAW_PASSWORD = "new secret";


    public static User createUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(TEST_EMAIL);
        user.setFirstname(TEST_FIRSTNAME);
        user.setLastname(TEST_LASTNAME);
        user.setPassword(TEST_RAW_PASSWORD);
        user.setRole(Role.USER);

        return user;
    }

    public static CurrentUserDto getCurrentUserHelper() {
        return new CurrentUserDto(USER_ID, TEST_EMAIL, Role.USER);
    }

    public static UserReadDto getUserReadHelper(Role role) {
        return new UserReadDto(USER_ID, TEST_EMAIL,
                role, TEST_FIRSTNAME,
                TEST_LASTNAME);
    }

    public static UserFilterDto getUserFilterHelper(Role role) {
        return new UserFilterDto(TEST_EMAIL, role);
    }

    public static UserUpdateDto getUserUpdateHelper() {
        return new UserUpdateDto(TEST_UPDATE_FNAME,
                TEST_UPDATE_LNAME, TEST_UPDATE_RAW_PASSWORD, TEST_UPDATE_EMAIL, Role.USER);
    }

}
