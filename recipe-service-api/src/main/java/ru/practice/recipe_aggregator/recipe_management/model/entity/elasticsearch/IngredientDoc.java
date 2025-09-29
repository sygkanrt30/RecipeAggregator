package ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

@Data
@Builder
@Document(indexName = "ingredient")
public class IngredientDoc {
    @Id
    @Field(type = FieldType.Text)
    private UUID id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String unit;

    @Field(type = FieldType.Text)
    private String quantity;

    @Field(type = FieldType.Keyword)
    private RecipeDoc recipe;
}
