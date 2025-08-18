package ru.practice.search_service.model.dto.container;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchContainer {
    final String name;
    final List<String> ingredientsName;
    int maxMins4Cook;
    int maxTotalMins;
    int maxMins4Prep;
    final int minServings;
    int maxServings;
}
