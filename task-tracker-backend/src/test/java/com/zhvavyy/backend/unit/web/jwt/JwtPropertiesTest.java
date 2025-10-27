package com.zhvavyy.backend.unit.web.jwt;

import com.zhvavyy.backend.web.jwt.JwtProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class JwtPropertiesTest {

    @InjectMocks
    private JwtProperties jwtProperties;
private String testPublicKey="-----BEGIN PUBLIC KEY-----\n" +
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwE0E+/2faxGPGCi/+a9L\n" +
        "Kh0lKLuYywJXH7DIj2TAdW6HMrukB4pOtvcQ+fjP2TgydpyqoOD+ECLOwIrzo8Bv\n" +
        "Ppd9XbhOl7gRuB153C5ksmFy07bOnsTRUzTGlkC/nmr73cMVGuT6EweC0f1BqcTm\n" +
        "BB/MMJ/PFeeUvYddv0wIRwykgG6Vx3Qy42axZ6Wx+zl+gQ2K638r44jKd4/qad0o\n" +
        "YoL7EvTIw3IcS0FpdHh41AmhNHhUA2aqNsBg48ROq13Xt9a3EYuwSlAOafpMdLy9\n" +
        "uN84FKYD/PqdHSPiaKTDzXhssu886K8rgmZCvb4BvrWMdAlRu5Cc4a6TsLdvXVwE\n" +
        "JQIDAQNN\n" +
        "-----END PUBLIC KEY-----";

    private String testPrivateKey= "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDATQT7/Z9rEY8Y\n" +
            "KL/5r0sqHSUou5jLAlcfsMiPZMB1bocyu6QHik629xD5+M/ZODJ2nKqg4P4QIs7A\n" +
            "ivOjwG8+l31duE6XuBG4HXncLmSyYXLTts6exNFTNMaWQL+eavvdwxUa5PoTB4LR\n" +
            "/UGpxOYFj8wwn88V55S9h12/TAhHDKSAbpXHdDLjZrFnpbH7OX6BDYrrfyvjiMp3\n" +
            "j+pp3ShigvsS9MjDchxLQWl0eHjUCaE0eFQDZqo2wGDjxE6rXde31rcRi7BKUA5p\n" +
            "+kx0vL243zgUpgP8+p0dI+JopMPNeGyy7zzoryuCZkK9vgG+tYx0CVG7kJzhrpOw\n" +
            "t29dXAQlAgMBAAECggEBAKhtjRUM8ek8SuG1J1gQobv0JQzFKrF8jkSSeUemzi7M\n" +
            "rRvRX3y/Nexo/3SB6cJuxzw+QfCAv+XwJrhbu8PME2N2GfbRK/0U2mgxYP9pnwbh\n" +
            "9UnbzqMe7dx72AfYFD0zpQB6Em0o6qNqK7myoSWJ8JIkfYqFHkMoP421F3YqqQFY\n" +
            "WVTnlWuq81ho6TKjSRiX0zfXHvUjgPwdVg3vO82AzDXFpEAhYOAWofInZ+nhLpUd\n" +
            "O8dDRjEbXheT9xYMTLsZqDqtOCoryz2EaHzsBBnSjQ53tXK+T3TI0zNo5X+cLpRj\n" +
            "20dooiXjMV72m2o2tkKBgg+4qfAQz0UCU6j/yEHYww0CgYEA9/ylnWX/AN7GCy13\n" +
            "mxaRuCeQW5VwMr9YP0lZtnCv4ZDQ47Ftwb5f4pmIwpOL1wAEVgRAijvwXmWsQGD2\n" +
            "JHci8O4EtTq4K2Y3qBXMddeNjrJOqkBzClVTfxfwhdahxFA6zOavglDIqMRISYON\n" +
            "OT0L8cvYtDQF4y6dd2kFwLlzAoMCgYEAxoO8ggmR/hqRoVrK89pIOAYWl2NIy234\n" +
            "1JfLAdY8Vr4iqrVw9KE3oCNTV3A9xKdkSf7PupiauS1QBRUywgCTB/xredNGwWIP\n" +
            "mti5P680I54EkYvhFR9pdt5S24SOyWpYymf165iM0CEahj6C95+1pvFfcyTsG/bX\n" +
            "MtO+rnfOfjcCgYEA5fD+H9RTfxsRx4uBO+zRcVA+Kq2GO4fw67230dLrQtxk1LvQ\n" +
            "abV8c0Cp7sGhCNqbKqcGsSai2uSrs4Y0tdtCKuSIuQKZgqRlNxX/X8VfHNF436Sj\n" +
            "BxHXOiGYhIFfvggmfZfpZYxCgzp9TK0OZeSVDr9VUMJsMxsmJ3LaQrmySsECgYEA\n" +
            "nOIlixsnHos+xibrt7WtV2EwX2necK6zdMeVbxkxTFgtK34DC8GRJWRli/evSQgF\n" +
            "ERlob44E0Q2HbMzo9Trg9/G7umaHR3NI49Yc6MB80x0hfSjvs/LENWXUU5m6AmGi\n" +
            "VVu+xPclaaKPxMIIXMwCEjI1fdkTI7J7Mo2y50zVixcCgYBMB/eg/B+OTCd4ILD1\n" +
            "kLbn+uM3FG0PRvXxEtOeACYVAN73ZRCPNsj4KEYgSk7LWW9GbpEfVvHSoojhZAIy\n" +
            "m4PQCQ6Qo1f3hqwAOXzdaLvTukzgCjBkWOhk2Dc9t0lbIcnplAoi/FxBK61jnj0S\n" +
            "hwdba0bbI9zw+wfwnltHhyD+1g==\n" +
            "-----END PRIVATE KEY-----";

    @Test
    public void publicKey() throws Exception {
        ReflectionTestUtils.setField(jwtProperties, "publicKeyContent", testPublicKey);
        RSAPublicKey rsaPublicKey = jwtProperties.publicKey();
        assertNotNull(rsaPublicKey);
    }

    @Test
    public void privateKey() throws Exception {
        ReflectionTestUtils.setField(jwtProperties, "privateKeyContent", testPrivateKey);
        RSAPrivateKey rsaPrivateKey = jwtProperties.privateKey();
        assertNotNull(rsaPrivateKey);
        assertNotNull(rsaPrivateKey.getModulus());
    }
}
