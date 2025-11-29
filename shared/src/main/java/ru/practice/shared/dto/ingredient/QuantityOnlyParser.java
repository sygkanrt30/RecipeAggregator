package ru.practice.shared.dto.ingredient;

final class QuantityOnlyParser {
    static IngredientDto parse(String text) {
        var matcher = IngredientPattern.QUANTITY_ONLY.pattern().matcher(text);
        if (matcher.matches()) {
            String quantity = matcher.group(1);
            String name = matcher.group(2).trim();
            var unit = "";

            var parenMatcher = IngredientPattern.PARENTHESES.pattern().matcher(name);
            if (parenMatcher.matches()) {
                String mainName = parenMatcher.group(1).trim();
                String parenContent = parenMatcher.group(2).trim();

                var parenQuantityUnit = IngredientPattern.QUANTITY_UNIT.pattern().matcher(parenContent);
                if (parenQuantityUnit.matches()) {
                    quantity = parenQuantityUnit.group(1);
                    unit = parenQuantityUnit.group(2);
                    name = mainName + " " + parenQuantityUnit.group(3).trim();
                } else {
                    name = mainName + " (" + parenContent + ")";
                }
            }
            return IngredientDto.of(name, quantity, unit);
        }
        return null;
    }
}
