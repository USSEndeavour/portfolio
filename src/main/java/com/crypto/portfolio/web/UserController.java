package com.crypto.portfolio.web;

import com.crypto.portfolio.entities.User;
import com.crypto.portfolio.services.UserService;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        Optional<User> user = service.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserNotFound");
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/telegramId/{telegramId}")
    public ResponseEntity<?> getUserByTelegramId(@PathVariable Long telegramId) {
        Optional<User> user = service.getUserByTelegramId(telegramId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserNotFound");
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/userName/{userName}")
    public ResponseEntity<?> getUserByUserName(@PathVariable String userName) {
        Optional<User> user = service.getUserByUserName(userName);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserNotFound");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        if (!service.getUserByTelegramId(user.getTelegramId()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The user already exists.");
        }
        User savedUser = service.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateHolder(@PathVariable Integer id, @RequestBody User user) {
        Optional<User> userById = service.getUserById(id);
        if (userById.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserNotFound");
        }
        User existingUser = userById.get();
        existingUser.setName(user.getName());
        existingUser.setTelegramId(user.getTelegramId());
        existingUser.setTelegramUserName(user.getTelegramUserName());
        existingUser.setComment(user.getComment());
        existingUser.setUserRole(user.getUserRole());
        existingUser.setTelegramGroupId(user.getTelegramGroupId());
        service.saveUser(existingUser);
        return ResponseEntity.ok(existingUser);
    }

    @GetMapping
    public ResponseEntity<?> getAllHolders() {
        List<User> result = StreamSupport.stream(service.getAllUsers().spliterator(), false)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteHolder(@PathVariable Integer id) {
        if (service.getUserById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        service.deleteUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
