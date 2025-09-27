package com.zhvavyy.backend.web.authorization;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtUtils jwtUtils;


    @Transactional
    public JwtResponse register(RegisterRequest registerRequest) {
        //вынести в валидацию
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
                .build();

        var customUserDetails = new CustomUserDetails(userRepository.save(user));
        var token = jwtUtils.generateToken(customUserDetails);

        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new JwtResponse(token);
    }


    //валидация!
    public JwtResponse login(LoginRequest loginRequest,
                             AuthenticationManager authenticationManager){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                       loginRequest.email(),
                       loginRequest.rawPassword()
                ));
               CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
               return  new JwtResponse(jwtUtils.generateToken(customUserDetails));
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return userRepository.findByEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("user not found with email: " + username));

    }
}
