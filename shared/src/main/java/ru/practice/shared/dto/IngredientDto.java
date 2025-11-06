package ru.practice.shared.dto;

public record IngredientDto(
        String name,
        String quantity,
        String unit) {
    private static final String DEFAULT_VALUE = "1";

    public static IngredientDto of(String name, String quantity, String unit) {
        if (quantity == null || quantity.isEmpty()) {
            quantity = DEFAULT_VALUE;
        }
        return new IngredientDto(name, quantity, unit);
    }
}
