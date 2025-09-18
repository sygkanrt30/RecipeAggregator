package ru.practice.user_service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import ru.practice.user_service.domain.TokenUser;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcTemplateUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserDetails> loadUserByUsername(String username) {
        var users = jdbcTemplate.query("""
        SELECT id, username, password, email, role
        FROM app_user
        WHERE username = ?
        """, (rs, rowNum) -> {
            var authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority(rs.getString("role")));
            return new TokenUser(
                    rs.getString("username"),
                    rs.getString("password"),
                    authorities,
                    rs.getString("email"),
                    null
            );
        }, username);

        if (users.isEmpty()) {
            return Optional.empty();
        }

        var user = users.getFirst();
        List<Long> favoriteRecipes = jdbcTemplate.query("""
        SELECT recipe_id
        FROM favorite_recipe
        WHERE user_id = (
            SELECT id FROM app_user WHERE username = ?
        )
        """, (rs, rowNum) -> rs.getLong("recipe_id"), username);

        user.getFavoriteRecipeIds().addAll(favoriteRecipes);
        return Optional.of(user);
    }

    @Override
    public void save(TokenUser user) {
        var userId = jdbcTemplate.queryForObject("""
                        INSERT INTO app_user (username, password, email, role, created_at)
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id
                        """, Long.class,
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse(null),
                Instant.now());

        if (!user.getFavoriteRecipeIds().isEmpty()) {
            var batchArgs = user.getFavoriteRecipeIds().stream()
                    .map(recipeId -> new Object[]{userId, recipeId, Instant.now()})
                    .toList();
            jdbcTemplate.batchUpdate("""
                    INSERT INTO favorite_recipe (user_id, recipe_id, added_at)
                    VALUES (?, ?, ?)
                    """, batchArgs);
        }
    }
}
