package ru.practice.user_service.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class TokenUser extends User {
    private final String email;
    private final List<Long> favoriteRecipeIds;

    public TokenUser(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String email,
            List<Long> favoriteRecipeIds
    ) {
        super(username, password, authorities);
        this.email = email;
        this.favoriteRecipeIds = favoriteRecipeIds == null ? new ArrayList<>() : favoriteRecipeIds;
    }

    public TokenUser(
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities,
            String email,
            List<Long> favoriteRecipeIds) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.email = email;
        this.favoriteRecipeIds = favoriteRecipeIds;
    }

}
