package ru.practice.user_service.repository;

import org.springframework.security.core.userdetails.UserDetails;
import ru.practice.user_service.domain.TokenUser;

import java.util.Optional;

public interface UserRepository {
    Optional<UserDetails> loadUserByUsername(String username);

    void save(TokenUser user);
}
