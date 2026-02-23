package com.pvp.travelmatch.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvp.travelmatch.entity.User;
import com.pvp.travelmatch.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestBody User user) {

        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "User Registered Successfully";
    }
}