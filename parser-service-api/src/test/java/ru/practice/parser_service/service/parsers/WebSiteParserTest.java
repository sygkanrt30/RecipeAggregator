package ru.practice.parser_service.service.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.parser_service.model.Recipe;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSiteParserTest {
    private WebSiteParser webSiteParser;

    @BeforeEach
    void setUp() {
        webSiteParser = new WebSiteParser(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36",
                3600,
                3,
                """
                        div.mntl-alphabetical-list__group,
                        div.comp.mntl-universal-card-list,
                        div.comp.mntl-taxonomysc-content,
                        div.comp.mntl-taxonomy-nodes,
                        div.comp.mntl-taxonomysc-article-list-group,
                        div.comp.related-category-search__category-tags,
                        div.comp.mntl-sc-block,
                        div.comp.mntl-three-post__inner
                        """,
                "https://www.google.com",
                "html[id=recipeScTemplate_1-0]"
        );
    }

    @Test
    void parseWebsite_shouldRespectMaxDepth() {
        // Arrange
        String level1Html = """
                <div class="mntl-universal-card-list">
                    <a href="https://www.allrecipes.com/level2"></a>
                </div>
                """;
        String level2Html = """
                <div class="mntl-taxonomysc-content">
                    <a href="https://www.allrecipes.com/level3"></a>
                </div>
                """;
        String level3Html = """
                <div class="mntl-sc-block">
                    <a href="https://www.allrecipes.com/level4"></a>
                </div>
                """;

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)) {
            Document l1Doc = Jsoup.parse(level1Html);
            Document l2Doc = Jsoup.parse(level2Html);
            Document l3Doc = Jsoup.parse(level3Html);
            String mainPageUrl = "https://www.allrecipes.com/recipes-a-z-6735880#alphabetical-list-a";
            mockJsoupConnection(mockedJsoup, mainPageUrl, l1Doc);
            mockJsoupConnection(mockedJsoup, "https://www.allrecipes.com/level2", l2Doc);
            mockJsoupConnection(mockedJsoup, "https://www.allrecipes.com/level3", l3Doc);

            // Act
            webSiteParser.parseWebsite(mainPageUrl);

            // Assert
            mockedJsoup.verify(() -> Jsoup.connect("https://www.allrecipes.com/level4"), never());
        }
    }

    @Test
    void parseWebsite_shouldSkipNonRecipePages() {
        // Arrange
        String nonRecipeHtml = "<html><body>Not a recipe</body></html>";

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class)) {
            Document doc = Jsoup.parse(nonRecipeHtml);
            String testUrl = "https://www.allrecipes.com/recipe/12345";
            mockJsoupConnection(mockedJsoup, testUrl, doc);

            // Act
            List<Recipe> result = webSiteParser.parseWebsite(testUrl);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    private void mockJsoupConnection(MockedStatic<Jsoup> mockedJsoup, String url, Document doc) {
        mockedJsoup.when(() -> Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36")
                        .referrer("https://www.google.com")
                        .timeout(15000)
                        .followRedirects(true)
                        .ignoreHttpErrors(true)
                        .get())
                .thenReturn(doc);
    }
}
