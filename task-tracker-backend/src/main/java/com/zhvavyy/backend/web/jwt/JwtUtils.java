package com.zhvavyy.backend.web.jwt;


import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtUtils{

    JwtEncoder jwtEncoder;
    JwtDecoder jwtDecoder;


    public String generateToken(CustomUserDetails userDetails) {
        JwsHeader jwsHeader = JwsHeader.with(()->"RS256").build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
               .subject(userDetails.getUsername())
               .claim("authorities", userDetails.getAuthorities())
               .issuedAt(Instant.now())
               .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
               .build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims));
        return jwt.getTokenValue();
    }


    public boolean checkToken(String token){
       try {
           jwtDecoder.decode(token);
           return true;
       }catch (JwtException e){
           throw new JwtException("token is invalid or expired");
       }
    }

    public String getUsername(String token){
       return jwtDecoder.decode(token)
                .getSubject();
    }

}