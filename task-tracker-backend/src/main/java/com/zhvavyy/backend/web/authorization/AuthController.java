package com.zhvavyy.backend.web.authorization;

import com.zhvavyy.backend.web.dto.JwtResponse;
import com.zhvavyy.backend.web.dto.LoginRequest;
import com.zhvavyy.backend.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> registration(@Valid @RequestBody RegisterRequest registerRequest){
            var jwtResponse= authService.register(registerRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtResponse.jwtToken())
                .body(jwtResponse);
    }


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginAuth(@Valid @RequestBody LoginRequest loginRequest){
   var response = authService.authenticate(loginRequest);
    return  ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION ,"Bearer " + response.jwtToken())
            .body(response);
    }
}
