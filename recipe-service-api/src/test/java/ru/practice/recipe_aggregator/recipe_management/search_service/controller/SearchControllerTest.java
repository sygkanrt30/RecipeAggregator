package ru.practice.recipe_aggregator.recipe_management.search_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterOperator;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RequestMapper;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.SearchService;
import ru.practice.recipe_aggregator.security.TestSecurityConfig;
import ru.practice.shared.dto.RecipeDto;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(SearchController.class)
@Import(TestSecurityConfig.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SearchService searchService;

    @MockitoBean
    private RequestMapper requestMapper;

    @Test
    void searchByName_ShouldReturnResults() throws Exception {
        var expectedResults = List.of(
                mock(RecipeDto.class),
                mock(RecipeDto.class)
        );
        when(searchService.searchByName("chicken")).thenReturn(expectedResults);

        mockMvc.perform(get("/api/v1/search/name/chicken")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedResults.size()))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
        verify(searchService, times(1)).searchByName("chicken");
    }

    @Test
    void searchByName_WithNoResults_ShouldReturnEmptyList() throws Exception {
        when(searchService.searchByName("nonexistent")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/search/name/nonexistent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void searchByIngredients_ShouldReturnResults() throws Exception {
        var ingredients = Set.of("chicken", "rice");
        var expectedResults = List.of(mock(RecipeDto.class), mock(RecipeDto.class));
        when(searchService.searchByIngredients(ingredients)).thenReturn(expectedResults);

        mockMvc.perform(post("/api/v1/search/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredients))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedResults.size()))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
        verify(searchService).searchByIngredients(ingredients);
    }

    @Test
    void searchByIngredients_WithNullBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/search/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchByIngredients_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/search/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchWithFiltering_ShouldReturnFilteredResults() throws Exception {
        var request = new SearchRequest(
                "pasta",
                Set.of("tomato", "cheese"),
                30,
                "LT",
                60,
                "LTE",
                15,
                "EQ",
                4,
                "GTE"
        );

        var container = SearchContainer.builder()
                .name("pasta")
                .ingredientNames(Set.of("tomato", "cheese"))
                .cookingTimeCondition(new FilterCondition("cookingTime", FilterOperator.LT, 30))
                .totalTimeCondition(new FilterCondition("totalTime", FilterOperator.LTE, 60))
                .preparationTimeCondition(new FilterCondition("preparingTime", FilterOperator.EQ, 15))
                .servingsCondition(new FilterCondition("servings", FilterOperator.GTE, 4))
                .build();

        var expectedResults = List.of(mock(RecipeDto.class));

        when(requestMapper.toSearchContainer(request)).thenReturn(container);
        when(searchService.searchWithFiltering(container)).thenReturn(expectedResults);

        mockMvc.perform(post("/api/v1/search/with-filtering")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedResults.size()));

        verify(requestMapper).toSearchContainer(request);
        verify(searchService).searchWithFiltering(container);
    }

    @Test
    void searchWithFiltering_WithNullValues_ShouldHandleCorrectly() throws Exception {
        var request = new SearchRequest(
                "pasta",
                null,
                0,
                null,
                0,
                null,
                0,
                null,
                0,
                null
        );

        var container = SearchContainer.builder()
                .name("pasta")
                .build();

        var expectedResults = List.of(mock(RecipeDto.class));

        when(requestMapper.toSearchContainer(request)).thenReturn(container);
        when(searchService.searchWithFiltering(container)).thenReturn(expectedResults);

        mockMvc.perform(post("/api/v1/search/with-filtering")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedResults.size()));

        verify(requestMapper).toSearchContainer(request);
        verify(searchService).searchWithFiltering(container);
    }

    @Test
    void searchWithFiltering_WithEmptyIngredientNames_ShouldHandleCorrectly() throws Exception {
        var request = new SearchRequest(
                "pasta",
                Collections.emptySet(),
                0,
                null,
                0,
                null,
                0,
                null,
                0,
                null
        );

        var container = SearchContainer.builder()
                .name("pasta")
                .ingredientNames(Collections.emptySet())
                .build();

        var expectedResults = List.of(mock(RecipeDto.class));

        when(requestMapper.toSearchContainer(request)).thenReturn(container);
        when(searchService.searchWithFiltering(container)).thenReturn(expectedResults);

        mockMvc.perform(post("/api/v1/search/with-filtering")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedResults.size()));

        verify(requestMapper).toSearchContainer(request);
        verify(searchService).searchWithFiltering(container);
    }

    @Test
    void searchWithFiltering_WithNullBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/search/with-filtering")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}