package ru.practice.shared.dto.ingredient;

final class ParenthesesParser {
    static IngredientDto parse(String text) {
        var matcher = IngredientPattern.PARENTHESES.pattern().matcher(text);
        if (matcher.matches()) {
            String mainPart = matcher.group(1).trim();
            String parenPart = matcher.group(2).trim();

            var mainMatcher = IngredientPattern.QUANTITY_UNIT.pattern().matcher(mainPart);
            if (mainMatcher.matches()) {
                String quantity = mainMatcher.group(1);
                String unit = mainMatcher.group(2);
                String name = mainMatcher.group(3).trim() + " (" + parenPart + ")";
                return IngredientDto.of(name, quantity, unit);
            }
        }
        return null;
    }
}
