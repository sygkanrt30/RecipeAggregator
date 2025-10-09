package ru.practice.parser_service.service.parsers.website;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practice.parser_service.model.Recipe;
import ru.practice.parser_service.service.exception.ParserException;
import ru.practice.parser_service.service.parsers.enums.InvalidRequestPrefix;
import ru.practice.parser_service.service.parsers.enums.ValidHtmlTag;
import ru.practice.parser_service.service.parsers.recipe.RecipeParser;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class WebsiteParserImpl implements WebsiteParser {
    private static final int TIMEOUT = 15000;
    private static final int MIN_DELAY_MS = 1000;
    private static final int MAX_DELAY_MS = 2000;
    private static final int MAX_LINKS_PER_PAGE = 50;

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

    private final Set<String> visitedUrls = new HashSet<>();
    private final Set<String> parsedRecipeUrls = new HashSet<>();
    private final List<Recipe> allRecipes = new ArrayList<>();

    @Override
    public List<Recipe> parse(String url) {
        var newRecipes = new ArrayList<Recipe>();
        long startTime = System.currentTimeMillis();
        parseWebsiteRecursive(url, 0, newRecipes);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Parsing completed in {} ms. {} new recipes found", duration, newRecipes.size());
        allRecipes.addAll(newRecipes);
        return newRecipes;
    }

    private void parseWebsiteRecursive(String url, int depth, List<Recipe> newRecipes) {
        if (shouldStopParsing(url, depth, newRecipes)) {
            return;
        }
        visitedUrls.add(url);
        try {
            var doc = getDocument(url);
            log.debug("URL processing (depth {}): {}", depth, url);
            if (isRecipePage(doc)) {
                processRecipePage(url, doc, newRecipes);
            }
            if (newRecipes.size() < maxRecipes) {
                findAndProcessLinks(doc, depth, newRecipes);
            }
        } catch (Exception e) {
            log.error("Error processing URL {}: {}", url, e.getMessage());
        }
    }

    private boolean shouldStopParsing(String url, int depth, List<Recipe> newRecipes) {
        if (newRecipes.size() >= maxRecipes) {
            return true;
        }
        if (depth > maxDepth) {
            log.debug("Maximum depth {} for URL exceeded: {}", maxDepth, url);
            return true;
        }
        if (visitedUrls.contains(url)) {
            log.debug("URL is already visited: {}", url);
            return true;
        }
        return false;
    }

    private void processRecipePage(String url, Document doc, List<Recipe> newRecipes) {
        String normalizedUrl = normalizeUrl(url);
        if (parsedRecipeUrls.contains(normalizedUrl)) {
            log.debug("Recipe has already been parsed before: {}", url);
            return;
        }
        try {
            var recipe = RecipeParser.parseRecipePage(doc);
            if (isNewRecipe(recipe, normalizedUrl)) {
                if (newRecipes.size() < maxRecipes) {
                    newRecipes.add(recipe);
                    parsedRecipeUrls.add(normalizedUrl);
                    if (newRecipes.size() % 5 == 0) {
                        log.info("Progress: {} new recipes found from {}", newRecipes.size(), maxRecipes);
                    }
                    if (newRecipes.size() >= maxRecipes) {
                        log.info("Reached limit in {} new recipes", maxRecipes);
                    }
                }
            } else {
                log.debug("Recipe '{}' was already parsed", recipe.name());
            }
        } catch (Exception e) {
            log.error("Error when parsing recipe with URL {}: {}", url, e.getMessage());
        }
    }

    private boolean isNewRecipe(Recipe recipe, String normalizedUrl) {
        if (parsedRecipeUrls.contains(normalizedUrl)) {
            return false;
        }
        return !isDuplicateRecipe(recipe);
    }

    private boolean isDuplicateRecipe(Recipe recipe) {
        return allRecipes.stream()
                .anyMatch(existing ->
                        existing.name().equalsIgnoreCase(recipe.name()));
    }

    private String normalizeUrl(String url) {
        try {
            var uri = new URI(url);
            String normalized = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null)
                    .normalize()
                    .toString();
            return normalized.endsWith("/") ? normalized.substring(0, normalized.length() - 1) : normalized;
        } catch (Exception e) {
            log.warn("Failed to normalize URL: {}, use the original", url);
            return url;
        }
    }

    private Document getDocument(String url) {
        try {
            Thread.sleep(MIN_DELAY_MS + (long) (Math.random() * (MAX_DELAY_MS - MIN_DELAY_MS)));
            return Jsoup.connect(url)
                    .userAgent(userAgent)
                    .referrer(referrer)
                    .timeout(TIMEOUT)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .maxBodySize(0)
                    .get();
        } catch (IOException e) {
            throw new ParserException("URL loading error:" + url, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ParserException("Parsing interrupted", e);
        }
    }

    private boolean isRecipePage(Document doc) {
        return !doc.select(recipeTag).isEmpty();
    }

    private void findAndProcessLinks(Document doc, int currentDepth, List<Recipe> newRecipes) {
        if (newRecipes.size() >= maxRecipes) {
            return;
        }
        int linksToProcess = Math.min(MAX_LINKS_PER_PAGE, maxRecipes - newRecipes.size());
        int processedLinks = 0;
        for (var tag : doc.select(tagClasses)) {
            if (processedLinks >= linksToProcess) {
                break;
            }
            for (var link : tag.select(ValidHtmlTag.HREF_TAG.value())) {
                if (processedLinks >= linksToProcess || newRecipes.size() >= maxRecipes) {
                    return;
                }
                String href = link.absUrl(ValidHtmlTag.HREF.value());
                if (isValidUrl(href) &&
                        !visitedUrls.contains(href) &&
                        !parsedRecipeUrls.contains(normalizeUrl(href))) {
                    parseWebsiteRecursive(href, currentDepth++, newRecipes);
                    processedLinks++;
                }
            }
        }
        log.debug("Processed {} link with pages", processedLinks);
    }

    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        for (var prefix : InvalidRequestPrefix.values()) {
            if (url.startsWith(prefix.value())) {
                return false;
            }
        }
        return url.startsWith(ValidHtmlTag.HTTP.value());
    }

    @Override
    public void reset() {
        visitedUrls.clear();
        parsedRecipeUrls.clear();
        allRecipes.clear();
        log.info("Parser state reset");
    }
}