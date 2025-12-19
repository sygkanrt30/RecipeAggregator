package ru.practice.parser_service.service.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.practice.parser_service.config.BrowserConfig;
import ru.practice.parser_service.config.ParserConfig;
import ru.practice.parser_service.service.cache.NameOfUrlCaches;
import ru.practice.parser_service.service.cache.RecipeCache;
import ru.practice.parser_service.service.cache.UrlCache;
import ru.practice.parser_service.service.parsers.recipe.ParserOrganizer;
import ru.practice.parser_service.service.parsers.website.WebsiteParserImpl;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WebSiteParserTest {

    private ParserOrganizer parserOrganizer;
    private UrlCache<NameOfUrlCaches, String> urlsCache;
    private WebsiteParserImpl webSiteParser;
    private MockedStatic<Jsoup> mockedJsoup;

    @BeforeEach
    void setUp() {
        parserOrganizer = mock(ParserOrganizer.class);
        urlsCache = mock(UrlCache.class);
        RecipeCache<String, RecipeDto> recipeCache = mock(RecipeCache.class);

        var parserConfig = new ParserConfig()
                .setTimeoutMs(15000)
                .setMinDelayMs(1000)
                .setMaxDelayMs(2000)
                .setMaxLinksPerPage(50)
                .setMaxRecipes(25)
                .setMaxDepth(3)
                .setContainerSelectors("div.recipe")
                .setRecipeTag("recipe");

        var browserConfig = new BrowserConfig()
                .setUserAgent("test-agent")
                .setReferrer("https://test.com");

        webSiteParser = new WebsiteParserImpl(parserConfig, browserConfig, parserOrganizer, urlsCache, recipeCache);
        mockedJsoup = mockStatic(Jsoup.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedJsoup != null) {
            mockedJsoup.close();
        }
    }

    @Test
    void parseWebsite_shouldRespectMaxDepth() {
        when(urlsCache.contains(any(NameOfUrlCaches.class), anyString())).thenReturn(false);

        var level1Html = """
                <div class="mntl-universal-card-list">
                    <a href="https://www.allrecipes.com/level2"></a>
                </div>
                """;
        var level2Html = """
                <div class="mntl-taxonomysc-content">
                    <a href="https://www.allrecipes.com/level3"></a>
                </div>
                """;
        var level3Html = """
                <div class="mntl-sc-block">
                    <a href="https://www.allrecipes.com/level4"></a>
                </div>
                """;

        Document l1Doc = Jsoup.parse(level1Html, "https://www.allrecipes.com/recipes-a-z-6735880#alphabetical-list-a");
        Document l2Doc = Jsoup.parse(level2Html, "https://www.allrecipes.com/level2");
        Document l3Doc = Jsoup.parse(level3Html, "https://www.allrecipes.com/level3");

        var mainPageUrl = "https://www.allrecipes.com/recipes-a-z-6735880#alphabetical-list-a";

        mockJsoupConnection(mainPageUrl, l1Doc);
        mockJsoupConnection("https://www.allrecipes.com/level2", l2Doc);
        mockJsoupConnection("https://www.allrecipes.com/level3", l3Doc);

        doThrow(new RuntimeException("Should not parse non-recipe pages"))
                .when(parserOrganizer).parseByPriority(any(Document.class));

        webSiteParser.parse(mainPageUrl);

        mockedJsoup.verify(() -> Jsoup.connect("https://www.allrecipes.com/level4"), never());
    }

    @Test
    void parseWebsite_shouldSkipNonRecipePages() {
        when(urlsCache.contains(any(NameOfUrlCaches.class), anyString())).thenReturn(false);

        var nonRecipeHtml = "<html><body>Not a recipe</body></html>";
        Document doc = Jsoup.parse(nonRecipeHtml, "https://www.allrecipes.com/recipe/12345");
        var testUrl = "https://www.allrecipes.com/recipe/12345";

        mockJsoupConnection(testUrl, doc);
        doThrow(new RuntimeException("Not a recipe page"))
                .when(parserOrganizer).parseByPriority(any(Document.class));

        List<RecipeDto> result = webSiteParser.parse(testUrl);

        assertTrue(result.isEmpty());
    }

    @Test
    void parseWebsite_shouldSkipAlreadyCachedUrls() {
        var testUrl = "https://www.allrecipes.com/recipe/cached-recipe";
        when(urlsCache.contains(eq(NameOfUrlCaches.VISITED_URLS), eq(testUrl)))
                .thenReturn(true);

        List<RecipeDto> result = webSiteParser.parse(testUrl);

        assertTrue(result.isEmpty());
        mockedJsoup.verify(() -> Jsoup.connect(anyString()), never());
    }

    private void mockJsoupConnection(String url, Document doc) {
        mockedJsoup.when(() -> Jsoup.connect(eq(url))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .referrer("https://www.google.com")
                        .timeout(15000)
                        .followRedirects(true)
                        .ignoreHttpErrors(true)
                        .get())
                .thenReturn(doc);
    }
}