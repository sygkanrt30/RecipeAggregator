package ru.practice.parser_service.service.parsers.website;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practice.parser_service.model.Recipe;
import ru.practice.parser_service.service.parsers.enums.InvalidRequestPrefix;
import ru.practice.parser_service.service.parsers.enums.ValidHtmlTag;
import ru.practice.parser_service.service.exception.ParserException;
import ru.practice.parser_service.service.parsers.recipe.RecipeParser;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class WebsiteParserImpl implements WebsiteParser {
    private static final int TIMEOUT = 15000;

    @Value("${parser.container.selectors}")
    private String tagClasses;
    @Value("${parser.max.recipes}")
    private int maxRecipes;
    @Value("${parser.max.depth}")
    private int maxDepth;
    @Value("${parser.website-with-recipe.browser-agent}")
    private String userAgent;
    @Value("${parser.website-with-recipe.referrer}")
    private String referrer;
    @Value("${parser.website-with-recipe.recipe-tag}")
    private String recipeTag;

    private final Set<String> visitedUrls;
    private final List<Recipe> recipes;
    private final AtomicInteger recipeCount;

    public WebsiteParserImpl() {
        this.visitedUrls = Collections.synchronizedSet(new HashSet<>());
        this.recipes = Collections.synchronizedList(new ArrayList<>());
        this.recipeCount = new AtomicInteger(0);
    }

    @Override
    public List<Recipe> parse(String url) {
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
                var recipe = RecipeParser.parseRecipePage(doc);
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
                    .timeout(TIMEOUT)
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
