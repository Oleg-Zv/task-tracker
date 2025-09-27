package com.zhvavyy.backend.web.jwt;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtProperties {

    @Value("${jwt.public-key}")
    private Resource publicKeyR;

    @Value("${jwt.private-key}")
    private Resource privateKeyR;

    @Bean
    public RSAPublicKey publicKey() throws Exception {
        return readPublicKey(publicKeyR);
    }

    @Bean
    public RSAPrivateKey privateKey() throws Exception {
        return readPrivateKey(privateKeyR);
    }

    private RSAPublicKey readPublicKey(Resource resource) throws Exception {
        String key = new String(resource.getInputStream().readAllBytes())
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replace("-----END RSA PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }

    private RSAPrivateKey readPrivateKey(Resource resource) throws Exception {
        String key = new String(resource.getInputStream().readAllBytes())
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }
    }

