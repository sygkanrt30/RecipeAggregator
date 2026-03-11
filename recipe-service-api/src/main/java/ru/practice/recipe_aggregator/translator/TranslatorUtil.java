package ru.practice.recipe_aggregator.translator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

@Component
public class TranslatorUtil {
    private final Translator translator;
    private final LanguageCode languageCode;

    public TranslatorUtil(Translator translator,
                          @Value("${web-site.lang}") String websiteLang) {
        this.translator = translator;
        this.languageCode = LanguageCode.fromCode(websiteLang);
    }

    public String translateTextDependingOnWebsiteLanguage(String text) {
        return switch (languageCode) {
            case ENGLISH -> text;
            case RUSSIAN -> translator.translate(text, LanguageCode.RUSSIAN.code(), LanguageCode.ENGLISH.code());
        };
    }

    public List<RecipeDto> translateDtoDependingOnWebsiteLanguage(List<RecipeDto> recipes) {
        return switch (languageCode) {
            case ENGLISH -> recipes;
            case RUSSIAN -> translator.translateListOfRecipeDtos(recipes,
                    LanguageCode.ENGLISH.code(), LanguageCode.RUSSIAN.code());
        };
    }
}
