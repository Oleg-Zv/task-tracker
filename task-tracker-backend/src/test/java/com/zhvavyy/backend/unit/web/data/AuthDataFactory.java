package com.zhvavyy.backend.unit.web.data;

import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.web.dto.RegisterRequest;


public final class AuthDataFactory {

    private AuthDataFactory(){}

    public static final Long USER_ID = 2L;
    public static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_FIRSTNAME = "test user";
    private static final String TEST_LASTNAME = "test user";
    public static final String TEST_RAW_PASSWORD = "secret";
    private static final String TEST_CONFIRM = "secret";

    public static User createUser() {
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setFirstname(TEST_FIRSTNAME);
        user.setLastname(TEST_LASTNAME);
        user.setPassword(TEST_RAW_PASSWORD);
        user.setRole(Role.USER);

        return user;
    }

    public static RegisterRequest createRegisterRequest(){
        RegisterRequest registerRequest =new  RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setFirstname(TEST_FIRSTNAME);
        registerRequest.setLastname(TEST_LASTNAME);
        registerRequest.setPassword(TEST_RAW_PASSWORD);
        registerRequest.setConfirmPassword(TEST_CONFIRM);
        registerRequest.setRole(Role.USER);
        return registerRequest;
    }

}
