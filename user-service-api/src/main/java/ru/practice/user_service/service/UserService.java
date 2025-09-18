package ru.practice.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.practice.user_service.domain.TokenUser;
import ru.practice.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements SaveUserService, FavoriteRecipeService, UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.loadUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public void save(String username, String password, String email) {
        var user = new TokenUser(username, password,
                List.of(new SimpleGrantedAuthority(Role.USER.name())), email, null);
        userRepository.save(user);
    }

    enum Role {
        USER
    }
}
