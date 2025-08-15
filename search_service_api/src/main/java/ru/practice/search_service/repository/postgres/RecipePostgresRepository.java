package ru.practice.search_service.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.search_service.model.entity.postgres.RecipeEntity;

@Repository
public interface RecipePostgresRepository extends JpaRepository<RecipeEntity, Long> {
}
