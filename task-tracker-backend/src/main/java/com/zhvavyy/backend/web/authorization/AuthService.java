package com.zhvavyy.backend.web.authorization;

import com.zhvavyy.backend.messaging.producer.RegisterProducer;
import com.zhvavyy.backend.messaging.producer.dto.DataForSendEmail;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.repository.UserRepository;
import com.zhvavyy.backend.web.dto.JwtResponse;
import com.zhvavyy.backend.web.dto.LoginRequest;
import com.zhvavyy.backend.web.dto.RegisterRequest;
import com.zhvavyy.backend.web.handler.PasswordIncorrectException;
import com.zhvavyy.backend.web.handler.UsernameAlreadyException;
import com.zhvavyy.backend.web.jwt.JwtUtils;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Transactional(readOnly = true)
public class AuthService {

    RegisterProducer producer;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtUtils jwtUtils;
    AuthenticationManager authenticationManager;

    @Transactional
    public JwtResponse register(RegisterRequest registerRequest) {

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UsernameAlreadyException("This email is already taken");
        }
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new PasswordIncorrectException("Passwords do not match");
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .firstname(registerRequest.getFirstname())
                .lastname(registerRequest.getLastname())
                .build();

        var customUserDetails = new CustomUserDetails(userRepository.save(user));
        var token = jwtUtils.generateToken(customUserDetails);

        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        DataForSendEmail data= DataForSendEmail.builder()
                .recipient(registerRequest.getEmail())
                .subject("Successful registration in Task Tracker")
                .msgBody("Hello " + registerRequest.getFirstname() +" !\n"+
                        "Your account has been registered.\n"+
                        "Good luck!")
                .build();
        producer.sendMessage(data);

        return new JwtResponse(token);
    }


    public JwtResponse authenticate(LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                       loginRequest.email(),
                       loginRequest.rawPassword()
                ));
       CustomUserDetails customUserDetails =(CustomUserDetails) authentication.getPrincipal();
       var token =jwtUtils.generateToken(customUserDetails);
       return new JwtResponse(token);
    }

}
