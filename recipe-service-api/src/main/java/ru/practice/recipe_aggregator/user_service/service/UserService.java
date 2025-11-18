package ru.practice.recipe_aggregator.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.user_service.exception.RegistrationException;
import ru.practice.recipe_aggregator.user_service.mapper.UserMapper;
import ru.practice.recipe_aggregator.user_service.model.User;
import ru.practice.recipe_aggregator.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements SaveUserService, GetUserInfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public User getUserByName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    @Transactional
    public void save(String username, byte[] password, String email) {
        var user = userMapper.fromCredentials(username, passwordEncoder.encode(new String(password)), email);
        try {
            userRepository.saveAndFlush(user);
        } catch (Exception e) {
            throw new RegistrationException(e.getMessage(), e);
        }
        log.info("Saved user: {}", user);
    }
}
