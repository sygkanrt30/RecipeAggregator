package ru.practice.search_service.model.entity.postgres;

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

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private int servings;

    @Column(nullable = false, length = 600)
    private String description;

    @Column(nullable = false, length = 3000)
    private String direction;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "recipe")
    private List<IngredientEntity> ingredients = new ArrayList<>();

    public void setIngredients(List<IngredientEntity> ingredients) {
        ingredients.forEach(phone -> phone.setRecipe(this));
        this.ingredients = ingredients;
    }

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
        return getName() != null && Objects.equals(getName(), recipe.getName());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
