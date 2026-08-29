package com.example.booking.config;

import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("User@123"))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }
    }
}
