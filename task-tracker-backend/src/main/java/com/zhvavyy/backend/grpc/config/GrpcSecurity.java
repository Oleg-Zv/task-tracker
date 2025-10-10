package com.zhvavyy.backend.grpc.config;


import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GrpcSecurity {

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
return  new BasicGrpcAuthenticationReader();
    }
    }
