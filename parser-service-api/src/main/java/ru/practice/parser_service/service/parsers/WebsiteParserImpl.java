package ru.practice.parser_service.service.parsers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practice.parser_service.model.Recipe;
import ru.practice.parser_service.service.enums.InvalidRequestPrefix;
import ru.practice.parser_service.service.enums.ValidHtmlTag;
import ru.practice.parser_service.service.exception.ParserException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class WebsiteParserImpl implements WebsiteParser {
    private final String tagClasses;
    private final int maxRecipes;
    private final int maxDepth;
    private final String userAgent;
    private final Set<String> visitedUrls;
    private final List<Recipe> recipes;
    private final AtomicInteger recipeCount;
    private final String referrer;
    private final String recipeTag;

    public WebsiteParserImpl(
            @Value("${parser.website-with-recipe.browser-agent}") String userAgent,
            @Value("${parser.max.recipes}") int maxRecipes,
            @Value("${parser.max.depth}") int maxDepth,
            @Value("${parser.container.selectors}") String selectorsConfig,
            @Value("${parser.website-with-recipe.referrer}") String referrer,
            @Value("${parser.website-with-recipe.recipe-tag}") String recipeTag) {

        this.visitedUrls = Collections.synchronizedSet(new HashSet<>());
        this.recipes = Collections.synchronizedList(new ArrayList<>());
        this.recipeCount = new AtomicInteger(0);
        this.userAgent = userAgent;
        this.maxRecipes = maxRecipes;
        this.maxDepth = maxDepth;
        this.tagClasses = selectorsConfig;
        this.referrer = referrer;
        this.recipeTag = recipeTag;
    }

    @Override
    public List<Recipe> parseWebsite(String url) {
        return parseWebsiteRecursive(url, 0);
    }

    private List<Recipe> parseWebsiteRecursive(String url, int depth) {
        if (recipeCount.get() >= maxRecipes || depth > maxDepth || visitedUrls.contains(url)) {
            return recipes;
        }

        visitedUrls.add(url);

        try {
            Document doc = getDocument(url);
            log.info("Processing URL (depth {}): {}", depth, url);
            if (isRecipePage(doc)) {
                Recipe recipe = RecipeParser.parseRecipePage(doc);

                synchronized (recipes) {
                    if (isCountOfRecipesAsMuchAsPossible()) {
                        recipes.add(recipe);
                        if (recipeCount.incrementAndGet() >= maxRecipes) {
                            log.info("Достигнут лимит в {} рецептов", maxRecipes);
                            return recipes;
                        }
                    }
                }
            }
            if (isCountOfRecipesAsMuchAsPossible()) {
                findAndProcessLinks(doc, depth);
            }
        } catch (Exception e) {
            log.error("Error processing URL {}: {}", url, e.getMessage());
        }
        return recipes;
    }

    private Document getDocument(String url) {
        try {
            Thread.sleep(1000 + (long) (Math.random() * 1000));
            return Jsoup.connect(url)
                    .userAgent(userAgent)
                    .referrer(referrer)
                    .timeout(15000)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .get();
        } catch (IOException e) {
            throw new ParserException("Error loading URL: " + url, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ParserException("Parser interrupted", e);
        }
    }

    private boolean isRecipePage(Document doc) {
        return !doc.select(recipeTag).isEmpty();
    }

    private boolean isCountOfRecipesAsMuchAsPossible() {
        return recipeCount.get() < maxRecipes;
    }

    private void findAndProcessLinks(Document doc, int currentDepth) {
        doc.select(tagClasses).parallelStream().forEach(tag -> {
            if (isCountOfRecipesAsMuchAsPossible()) {
                tag.select(ValidHtmlTag.HREF_TAG.value()).parallelStream()
                        .filter(link -> isValidUrl(link.absUrl(ValidHtmlTag.HREF.value())))
                        .filter(link -> !visitedUrls.contains(link.absUrl(ValidHtmlTag.HREF.value())))
                        .limit(maxRecipes - recipeCount.get())
                        .forEach(link -> {
                            String href = link.absUrl(ValidHtmlTag.HREF.value());
                            parseWebsiteRecursive(href, currentDepth + 1);
                        });
            }
        });
    }

    private boolean isValidUrl(String url) {
        return url != null && !url.startsWith(InvalidRequestPrefix.JAVASCRIPT.value())
                && !url.startsWith(InvalidRequestPrefix.MAILTO.value())
                && !url.startsWith(InvalidRequestPrefix.TEL.value())
                && url.startsWith(ValidHtmlTag.HTTP.value());
    }
}
