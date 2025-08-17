package ru.practice.search_service.model.dto.container;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchContainer(
        String name,
        List<String> ingredientsName,
        int maxMins4Cook,
        int maxTotalMins,
        int maxMins4Prep,
        int minServings,
        int maxServings
) {
}
