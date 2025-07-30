package ru.practice.recipe_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "recipe")
public class RecipeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String name;

    private int servings;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false, length = 300)
    private String direction;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "recipe")
    private List<IngredientEntity> ingredients = new ArrayList<>();

    @Column(name = "mins_for_preparing")
    private int mins4Prep;

    @Column(name = "mins_for_cooking")
    private int mins4Cook;

    @Column(name = "additional_mins")
    private int additionalMins;

    @Column(name = "total_mins")
    private int totalMins;

    @CreationTimestamp
    private Timestamp createdAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        RecipeEntity recipe = (RecipeEntity) o;
        return getId() != null && Objects.equals(getId(), recipe.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
