package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration 
public class DataInitializerConfig {
    @Bean
    public CommandLineRunner testUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            createUserIfNotFound(userRepository, passwordEncoder, "testA", "12456");
            createUserIfNotFound(userRepository, passwordEncoder, "testB", "12457");
        };
    }

        private void createUserIfNotFound (UserRepository userRepository, PasswordEncoder passwordEncoder, String username, String password){

            if (!userRepository.findByUsername(username).isPresent()){
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                System.out.println(user.getUsername() + "criado");
            }
        }
        
        

    }


