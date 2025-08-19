//// 11. NEW FILE: DataInitializer.java - Creates default admin user
//package com.bookstore.config;
//
//import com.bookstore.entity.Role;
//import com.bookstore.entity.User;
//import com.bookstore.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Create default admin user if not exists
//        if (userRepository.findByUsername("admin").isEmpty()) {
//            User adminUser = User.builder()
//                    .username("admin")
//                    .password(passwordEncoder.encode("admin123"))
//                    .role(Role.ADMIN)
//                    .build();
//
//            userRepository.save(adminUser);
//            log.info("Default admin user created - Username: admin, Password: admin123");
//        }
//    }
//}