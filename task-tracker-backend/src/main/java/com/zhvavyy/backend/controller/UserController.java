package com.zhvavyy.backend.controller;

import com.zhvavyy.backend.dto.CurrentUserDto;
import com.zhvavyy.backend.service.UserService;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/app/v1/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PreAuthorize("hasAuthority('ADMIN') or principal != null")
    @GetMapping("/current")
    public ResponseEntity<CurrentUserDto> getCurrentUser(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        var user =userService.getUser(userDetails);
          return ResponseEntity.ok(user);
    }
}
