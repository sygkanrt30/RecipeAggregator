package ru.practice.recipe_aggregator.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.RecipeService;
import ru.practice.recipe_aggregator.user_service.model.Role;
import ru.practice.recipe_aggregator.user_service.model.User;
import ru.practice.recipe_aggregator.user_service.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements SaveUserService, FavoriteRecipeService, UserDetailsService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecipeService recipeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByName(username);
    }

    private User getUserByName(String username) {
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
                .createdAt(Instant.now())
                .build();
        userRepository.saveAndFlush(user);
        log.info("Saved user: {}", user);
    }

    @Override
    public void add2Favorites(String username, String recipeName) {
        UUID recipeId = recipeService.getIdByName(recipeName);
        var user = getUserByName(username);
        if (user.getFavoriteRecipeIds().contains(recipeId)) {
            log.warn("User {} already has favorite recipe {}", username, recipeId);
            return;
        }
        user.getFavoriteRecipeIds().add(recipeId);
        log.info("Add recipe {} to favorite recipes {}", username, recipeId);

    }

    @Override
    public void removeFromFavorites(String username, String recipeName) {
        UUID recipeId = recipeService.getIdByName(recipeName);
        var user = getUserByName(username);
        boolean isRemoved = user.getFavoriteRecipeIds().remove(recipeId);
        if (!isRemoved) {
            throw new RecipeNotContainsException("Recipe not contains in favorite recipes " + recipeId);
        }
    }

    @Override
    public List<RecipeResponseDto> getFavorites(String username) {
        var favoriteRecipeIds = getUserByName(username).getFavoriteRecipeIds();
        return recipeService.findAllByIds(favoriteRecipeIds);
    }
}
