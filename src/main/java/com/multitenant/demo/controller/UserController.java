package com.multitenant.demo.controller;

import com.multitenant.demo.entity.User;
import com.multitenant.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.ok("user added");
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        var response = userRepository.findAll();
        return ResponseEntity.ok(response);
    }
}
