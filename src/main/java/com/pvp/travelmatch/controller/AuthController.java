package com.pvp.travelmatch.controller;

import com.pvp.travelmatch.dto.LoginRequest;
import com.pvp.travelmatch.entity.User;
import com.pvp.travelmatch.repository.UserRepository;
import com.pvp.travelmatch.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "User Registered Successfully";
    }


    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user.getEmail());
    }
}