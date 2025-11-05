package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class UserAuthServiceTest {

    @Mock
    private UserRepository userRepository;


    private User userTest;

    @InjectMocks
    private  UserAuthService userAuthService;


    @BeforeEach
    void setUpUserAuthService() {

        this.userTest = new User();
        this.userTest.setId(1L);
        this.userTest.setUsername("test");
        this.userTest.setPassword("1234");

    }


    @Test
    public void shouldLoadUserByUsername() {

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(this.userTest));
        UserDetails actualUserDetails = userAuthService.loadUserByUsername("test");
        assertThat(actualUserDetails).isNotNull();
        assertThat(actualUserDetails.getUsername()).isEqualTo("test");
        assertThat(actualUserDetails.getPassword()).isEqualTo("1234");

    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        assertThatThrownBy(() ->  userAuthService.loadUserByUsername("nonexistentuser")).isInstanceOf(UsernameNotFoundException.class);
        verify(userRepository, Mockito.times(1)).findByUsername("nonexistentuser");
    }
}
