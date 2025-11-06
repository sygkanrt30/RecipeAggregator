package ru.practice.recipe_aggregator.user_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.practice.recipe_aggregator.user_service.token.Token;

import java.time.Instant;
import java.util.*;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
@ToString
@Table(name = "app_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30, name = "username")
    private String username;

    @Column(nullable = false, length = 100, name = "password")
    @ToString.Exclude
    private String password;

    @Column(nullable = false, length = 100, unique = true, name = "email")
    @ToString.Exclude
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private Role role;

    @Column(name = "created_at")
    @CreationTimestamp
    @ToString.Exclude
    private Instant createdAt;

    @ElementCollection
    @CollectionTable(
            name = "favorite_recipe",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "recipe_id")
    @ToString.Exclude
    private List<UUID> favoriteRecipeIds = new ArrayList<>();

    @Transient
    @ToString.Exclude
    private Token token;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
