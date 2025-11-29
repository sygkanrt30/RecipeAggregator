package ru.practice.recipe_aggregator.translator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practice.shared.dto.RecipeDto;
import ru.practice.shared.dto.ingredient.IngredientDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practice.recipe_aggregator.translator.TranslatorConfig.*;

@Component
@Slf4j
class YandexTranslator implements Translator {
    private final String apiKey;
    private final String folderId;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String translateURI;

    YandexTranslator(@Value("${yandex.api.key}") String apiKey,
                     @Value("${yandex.api.folder-id}") String folderId,
                     @Value("${yandex.uri}") String translateURI) {
        this.apiKey = apiKey;
        this.folderId = folderId;
        this.translateURI = translateURI;
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    @Override
    public String translate(String text, String sourceLang, String targetLang) {
        try {
            JsonNode root = getRoot(sourceLang, targetLang, new String[]{text});
            String translatedText = root.get("translations").get(0).get("text").asText();
            log.debug("translated text: {}", translatedText);
            return translatedText;
        } catch (Exception e) {
            throw new TranslateException(e.getMessage(), e);
        }
    }

    private JsonNode getRoot(String sourceLang, String targetLang, String[] text)
            throws IOException, InterruptedException {

        var requestBody = getRequestBody(sourceLang, targetLang, text);

        String jsonBody = mapper.writeValueAsString(requestBody);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(translateURI))
                .header(Header.CONTENT_TYPE.value(), "application/json")
                .header(Header.AUTHORIZATION.value(), "Api-Key " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readTree(response.body());
    }

    private HashMap<String, Object> getRequestBody(String sourceLang, String targetLang, String[] text) {
        var requestBody = new HashMap<String, Object>();
        requestBody.put(SOURCE_LANGUAGE_CODE.key(), sourceLang);
        requestBody.put(TARGET_LANGUAGE_CODE.key(), targetLang);
        requestBody.put(TEXTS.key(), text);
        requestBody.put(FOLDER_ID.key(), folderId);
        return requestBody;
    }

    @Override
    public List<RecipeDto> translateListOfRecipeDtos(List<RecipeDto> recipes, String sourceLang, String targetLang) {
        return recipes.stream()
                .map(recipeDto -> translateDto(recipeDto, sourceLang, targetLang))
                .collect(Collectors.toList());
    }

    private RecipeDto translateDto(RecipeDto recipeDto, String sourceLang, String targetLang) {
        List<String> allTexts = collectAllTexts(recipeDto);

        if (allTexts.isEmpty()) {
            return recipeDto;
        }

        try {
            List<String> translations = bulkTranslate(allTexts, sourceLang, targetLang);
            RecipeDto translatedDto = buildTranslatedDto(recipeDto, translations);
            log.debug("translatedDto: {}", translatedDto.toString());
            return translatedDto;
        } catch (Exception e) {
            throw new TranslateException(e.getMessage(), e);
        }
    }

    private List<String> collectAllTexts(RecipeDto recipeDto) {
        var texts = new ArrayList<String>();

        if (recipeDto.name() != null && !recipeDto.name().trim().isEmpty()) {
            texts.add(recipeDto.name());
        }
        if (recipeDto.direction() != null && !recipeDto.direction().trim().isEmpty()) {
            texts.add(recipeDto.direction());
        }
        if (recipeDto.description() != null && !recipeDto.description().trim().isEmpty()) {
            texts.add(recipeDto.description());
        }

        if (recipeDto.ingredients() != null) {
            for (IngredientDto ingredient : recipeDto.ingredients()) {
                if (ingredient.name() != null && !ingredient.name().trim().isEmpty()) {
                    texts.add(ingredient.name());
                }
                if (ingredient.unit() != null && !ingredient.unit().trim().isEmpty()) {
                    texts.add(ingredient.unit());
                }
            }
        }

        return texts;
    }

    private List<String> bulkTranslate(List<String> texts, String sourceLang, String targetLang) throws Exception {
        JsonNode root = getRoot(sourceLang, targetLang, texts.toArray(new String[0]));
        JsonNode translationsNode = root.get("translations");

        List<String> translations = new ArrayList<>();
        for (JsonNode translation : translationsNode) {
            translations.add(translation.get("text").asText());
        }

        return translations;
    }

    private RecipeDto buildTranslatedDto(RecipeDto original, List<String> translations) {
        int index = 0;

        String translatedName = original.name();
        if (original.name() != null && !original.name().trim().isEmpty()) {
            translatedName = translations.get(index++);
        }

        String translatedDirection = original.direction();
        if (original.direction() != null && !original.direction().trim().isEmpty()) {
            translatedDirection = translations.get(index++);
        }

        String translatedDescription = original.description();
        if (original.description() != null && !original.description().trim().isEmpty()) {
            translatedDescription = translations.get(index++);
        }

        var translatedIngredients = new ArrayList<IngredientDto>();
        if (original.ingredients() != null) {
            for (IngredientDto ingredient : original.ingredients()) {
                String translatedIngredientName = ingredient.name();
                if (ingredient.name() != null && !ingredient.name().trim().isEmpty()) {
                    translatedIngredientName = translations.get(index++);
                }

                String translatedIngredientUnit = ingredient.unit();
                if (ingredient.unit() != null && !ingredient.unit().trim().isEmpty()) {
                    translatedIngredientUnit = translations.get(index++);
                }


                IngredientDto ingredientDto = IngredientDto.of(
                        translatedIngredientName,
                        ingredient.quantity(),
                        translatedIngredientUnit
                );
                translatedIngredients.add(ingredientDto);
            }
        }

        return RecipeDto.builder()
                .id(original.id())
                .name(translatedName)
                .timeForPreparing(original.timeForPreparing())
                .timeForCooking(original.timeForCooking())
                .totalTime(original.totalTime())
                .servings(original.servings())
                .ingredients(translatedIngredients)
                .direction(translatedDirection)
                .description(translatedDescription)
                .build();
    }
}