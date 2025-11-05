package com.example.demo.service;


import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.SignatureException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TokenServiceTest {

    private TokenService tokenService;
    private UserDetails testUserDetails;

    @BeforeEach
    public void setUpTokenService(){
        String testSecretKey = "my-test-secret-key-that-is-definitely-long-enough-for-hs256";
        Long testExpiration = 36000L;

        this.tokenService = new TokenService();
        ReflectionTestUtils.setField(this.tokenService, "secretKey", testSecretKey );
        ReflectionTestUtils.setField(this.tokenService,"expiration",testExpiration);

        this.testUserDetails = User.builder()
                .username("admin")
                .password("admin")
                .build();
    }

    @Test
    public void testGenerateTokenStructure(){


       String generatedToken = tokenService.generateToken(testUserDetails);

        assertThat(generatedToken).isNotNull().isNotEmpty();

        assertThat(generatedToken.replaceAll("[^.]","")
               .length())
               .isEqualTo(2);
    }

    @Test
    public void testUsernameExtracted(){

        String generatedToken = tokenService.generateToken(testUserDetails);

        String extractedUsername = tokenService.getSubject(generatedToken);

        assertThat(extractedUsername).isEqualTo(testUserDetails.getUsername());
    }

    @Test
    public void shouldReturnTrueIfTokenIsValid(){
        String token = tokenService.generateToken(testUserDetails);
        boolean valid = tokenService.isTokenValid(token, testUserDetails);
        assertThat(valid).isTrue();

    }

    @Test
    public void shouldReturnFalseIfTokenIsExpired() throws InterruptedException {


        ReflectionTestUtils.setField(this.tokenService,"expiration",1L);
        String token = tokenService.generateToken(testUserDetails);
        Thread.sleep(10);

            boolean valid = tokenService.isTokenValid(token, testUserDetails);
            assertThat(valid).isFalse();

    }

    @Test
    public void shouldReturnFalseForTokenWithWrongSubject(){


        UserDetails userA = User.builder().username("userA").password("password").build();
        UserDetails userB = User.builder().username("userB").password("password").build();

        String token = tokenService.generateToken(userA);

        boolean valid = tokenService.isTokenValid(token, userB);
        assertThat(valid).isFalse();
    }


}
