package com.crypto.portfolio.services;

import com.crypto.portfolio.entities.CashOfficeOperation;
import com.crypto.portfolio.entities.User;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.crypto.portfolio.repositories.UserRepository;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.lang.Iterable;

@Service
public class UserService {

    private RestTemplate defaultClient = new RestTemplate();

    private String updateUserById = "http://localhost:8080/users/update/{id}";

    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public User saveUser(User user){
        return repository.save(user);
    }

    public Optional<User> getUserById(Integer id) {
        return repository.findById(id);
    }

    public Optional<User> getUserByTelegramUserName(String telegramUserName) {
        return repository.findByTelegramUserName(telegramUserName);
    }

    public Optional<User> getUserByUserName(String userName) {
        return repository.findByName(userName);
    }

    public Iterable<User> getAllUsers() {
        return repository.findAll();
    }

    public void deleteUserById(Integer id) {
        repository.deleteById(id);
    }
}
