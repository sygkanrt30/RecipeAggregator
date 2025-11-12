package ru.practice.recipe_aggregator.user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRecipeId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "recipe_id")
    private UUID recipeId;
}
