package com.zhvavyy.backend.web.jwt;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtProperties {

    @Value("${jwt.public-key}")
    private String publicKeyContent;

    @Value("${jwt.private-key}")
    private String privateKeyContent;

    @Bean
    public RSAPublicKey publicKey() throws Exception {
        String key = publicKeyContent
                .replaceAll("-----.*KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }

    @Bean
    public RSAPrivateKey privateKey() throws Exception {
        String key = privateKeyContent
                .replaceAll("-----.*KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }
    }