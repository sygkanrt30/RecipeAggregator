package ru.practice.recipe_aggregator.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.user_service.model.Role;
import ru.practice.recipe_aggregator.user_service.model.User;
import ru.practice.recipe_aggregator.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements SaveUserService, GetUserInfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserByName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    @Transactional
    public void save(String username, String password, String email) {
        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role(Role.USER)
                .build();
        userRepository.saveAndFlush(user);
        log.info("Saved user: {}", user);
    }
}
