package com.crypto.portfolio.repositories;

import org.checkerframework.checker.nullness.Opt;
import org.springframework.data.repository.CrudRepository;
import com.crypto.portfolio.entities.User;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByTelegramId(Long telegramId);
    Optional<User> findByName(String name);
}
