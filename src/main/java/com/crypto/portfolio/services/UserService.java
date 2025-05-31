package com.crypto.portfolio.services;

import com.crypto.portfolio.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.crypto.portfolio.repositories.UserRepository;

import java.util.Optional;
import java.lang.Iterable;

@Service
public class UserService {

    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public void addUser(User user){
        repository.save(user);
    }

    public Optional<User> getUserById(Integer id) {
        return repository.findById(id);
    }

    public Optional<User> getUserByTelegramId(Long id) {
        return repository.findByTelegramId(id);
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
