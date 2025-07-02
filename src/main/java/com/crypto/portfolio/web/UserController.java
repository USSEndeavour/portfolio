package com.crypto.portfolio.web;

import com.crypto.portfolio.entities.User;
import com.crypto.portfolio.services.UserService;

import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.lang.Iterable;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Optional<User> getHolder(@PathVariable Integer id) {
        return service.getUserById(id);
    }

    @GetMapping("/telegramId/{telegramId}")
    public Optional<User> getHolderByTelegramId(@PathVariable Long telegramId) {
        return service.getUserByTelegramId(telegramId);
    }

    @GetMapping("/userName/{userName}")
    public Optional<User> getHolderByUserName(@PathVariable String userName) {
        return service.getUserByUserName(userName);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHolder(@RequestBody User user) {
        service.saveUser(user);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User updateHolder(@PathVariable Integer id, @RequestBody User user) {
        User userObject = null;
        try {
            userObject = (service.getUserById(id)).get();
            userObject.setName(user.getName());
            userObject.setTelegramId(user.getTelegramId());
            userObject.setTelegramUserName(user.getTelegramUserName());
            userObject.setComment(user.getComment());
            userObject.setUserRole(user.getUserRole());
            userObject.setTelegramGroupId(user.getTelegramGroupId());
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
        }
        return service.saveUser(userObject);
    }

    @GetMapping
    public Iterable<User> getAllHolders(){
        return service.getAllUsers();
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHolder(@PathVariable Integer id) {
        service.deleteUserById(id);
    }
}
