package com.zhvavyy.backend.web.authorization;

import com.zhvavyy.backend.repository.UserRepository;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)  {
        return userRepository.findByEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("user not found with email: " + username));

    }
}
