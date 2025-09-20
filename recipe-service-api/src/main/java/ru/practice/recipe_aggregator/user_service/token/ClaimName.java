package ru.practice.recipe_aggregator.user_service.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum ClaimName {
    AUTHORITIES("authorities");

    private final String name;
}
