package ru.practice.parser_service.service.parsers.website;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.practice.parser_service.config.RecipeParserConfig;
import ru.practice.parser_service.service.exception.ParserException;
import ru.practice.parser_service.service.parsers.enums.InvalidRequestPrefix;
import ru.practice.parser_service.service.parsers.enums.ValidHtmlTag;
import ru.practice.parser_service.service.parsers.recipe.ParserOrganizer;
import ru.practice.shared.dto.RecipeDto;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsiteParserImpl implements WebsiteParser {

    private final RecipeParserConfig parserConfig;
    private final ParserOrganizer parserOrganizer;
    private final Set<String> visitedUrlsCache = new HashSet<>();
    private final Set<String> parsedRecipeUrlsCache = new HashSet<>();
    private final Set<RecipeDto> recipeCache = new HashSet<>();

    @Override
    public List<RecipeDto> parse(String url) {
        log.debug("parse(url) Start parsing website from url: {}", url);
        var newRecipes = new ArrayList<RecipeDto>();
        long startTime = System.currentTimeMillis();
        parseWebsiteRecursive(url, 0, newRecipes);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Parsing completed in {} ms. {} new recipes found", duration, newRecipes.size());
        recipeCache.addAll(newRecipes);
        return newRecipes;
    }

    private void parseWebsiteRecursive(String url, int depth, List<RecipeDto> newRecipes) {
        if (shouldStopParsing(url, depth, newRecipes)) {
            return;
        }
        visitedUrlsCache.add(url);
        try {
            var doc = getDocument(url);
            log.debug("URL processing (depth {}): {}", depth, url);
            if (isRecipePage(doc)) {
                processRecipePage(url, doc, newRecipes);
            }
            if (newRecipes.size() < parserConfig.maxRecipes()) {
                findAndProcessLinks(doc, depth, newRecipes);
            }
        } catch (Exception e) {
            log.error("Error processing URL {}: {}", url, e.getMessage(), e);
        }
    }

    private boolean shouldStopParsing(String url, int depth, List<RecipeDto> newRecipes) {
        if (newRecipes.size() >= parserConfig.maxRecipes()) {
            return true;
        }
        if (depth > parserConfig.maxDepth()) {
            log.debug("Maximum depth {} for URL exceeded: {}", parserConfig.maxDepth(), url);
            return true;
        }
        if (visitedUrlsCache.contains(url)) {
            log.debug("URL is already visited: {}", url);
            return true;
        }
        return false;
    }

    private void processRecipePage(String url, Document doc, List<RecipeDto> newRecipes) {
        String normalizedUrl = normalizeUrl(url);
        try {
            var recipe = parserOrganizer.parseByPriority(doc);
            if (isNewRecipe(recipe, normalizedUrl)) {
                if (newRecipes.size() < parserConfig.maxRecipes()) {
                    newRecipes.add(recipe);
                    parsedRecipeUrlsCache.add(normalizedUrl);
                    if (newRecipes.size() % 5 == 0) {
                        log.debug("Progress: {} new recipes found from {}", newRecipes.size(), parserConfig.maxRecipes());
                    }
                    if (newRecipes.size() >= parserConfig.maxRecipes()) {
                        log.debug("Reached limit in {} new recipes", parserConfig.maxRecipes());
                    }
                }
            } else {
                log.debug("Recipe '{}' was already parsed", recipe.name());
            }
        } catch (Exception e) {
            log.error("Error when parsing recipe with URL: {}", e.getMessage());
        }
    }

    private boolean isNewRecipe(RecipeDto recipe, String normalizedUrl) {
        if (parsedRecipeUrlsCache.contains(normalizedUrl)) {
            return false;
        }
        return !recipeCache.contains(recipe);
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
            Thread.sleep(parserConfig.minDelayMs() +
                    (long) (Math.random() * (parserConfig.maxDelayMs() - parserConfig.minDelayMs())));
            var doc = Jsoup.connect(url)
                    .userAgent(parserConfig.userAgent())
                    .referrer(parserConfig.referrer())
                    .timeout(parserConfig.timeout())
                    .header("Accept", parserConfig.accept())
                    .header("Cookie", parserConfig.cookie())
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .maxBodySize(0)
                    .get();
            if (isCloudflarePage(doc)) {
                log.debug("Cloudflare found in url: {}", url);
            }
            return doc;
        } catch (IOException e) {
            throw new ParserException("URL loading error:" + url, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ParserException("Parsing interrupted", e);
        }
    }


    private boolean isCloudflarePage(Document doc) {
        return doc.title().contains("Один момент") ||
                doc.title().contains("Just a moment") ||
                !doc.select("script[src*='cloudflare']").isEmpty() ||
                !doc.select("script[src*='turnstile']").isEmpty() ||
                doc.text().contains("Cloudflare") ||
                doc.text().contains("Проверяем, человек ли вы");
    }

    private boolean isRecipePage(Document doc) {
        return !doc.select(parserConfig.recipeTag()).isEmpty();
    }

    private void findAndProcessLinks(Document doc, int currentDepth, List<RecipeDto> newRecipes) {
        if (newRecipes.size() >= parserConfig.maxRecipes()) {
            return;
        }
        int linksToProcess = Math.min(parserConfig.maxLinksPerPage(), parserConfig.maxRecipes() - newRecipes.size());
        int processedLinks = 0;
        for (var tag : doc.select(parserConfig.containerSelectors())) {
            if (processedLinks >= linksToProcess) {
                break;
            }
            for (var link : tag.select(ValidHtmlTag.HREF_TAG.value())) {
                if (processedLinks >= linksToProcess || newRecipes.size() >= parserConfig.maxRecipes()) {
                    return;
                }
                String href = link.absUrl(ValidHtmlTag.HREF.value());
                if (isValidUrl(href) &&
                        !visitedUrlsCache.contains(href) &&
                        !parsedRecipeUrlsCache.contains(normalizeUrl(href))) {
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
}