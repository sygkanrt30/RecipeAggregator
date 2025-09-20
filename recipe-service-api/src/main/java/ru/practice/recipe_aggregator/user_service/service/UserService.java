package ru.practice.recipe_aggregator.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.user_service.model.Role;
import ru.practice.recipe_aggregator.user_service.model.User;
import ru.practice.recipe_aggregator.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements SaveUserService, FavoriteRecipeService, UserDetailsService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public void save(String username, String password, String email) {
        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role(Role.USER)
                .build();
        userRepository.saveAndFlush(user);
    }
}
