package ru.practice.recipe_aggregator.model.entity.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "recipe")
public class RecipeDoc {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Integer)
    private int mins4Prep;

    @Field(type = FieldType.Integer)
    private int mins4Cook;

    @Field(type = FieldType.Integer)
    private int additionalMins;

    @Field(type = FieldType.Integer)
    private int totalMins;

    @Field(type = FieldType.Integer)
    private int servings;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<IngredientDoc> ingredients;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String direction;
}
