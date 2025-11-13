//package ru.practice.recipe_aggregator.recipe_management.search_service.search.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
//import ru.practice.recipe_aggregator.recipe_management.search_service.controller.SearchController;
//import ru.practice.recipe_aggregator.recipe_management.search_service.search.SearchService;
//import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterService;
//import ru.practice.shared.dto.RecipeDto;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.argThat;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//@WebMvcTest(SearchController.class)
//class SearchControllerTest {
//    private static final String TEST_USER = "testuser";
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private SearchService searchService;
//
//    @MockitoBean
//    private FilterService filterService;
//
//    @Test
//    @WithMockUser(username = TEST_USER)
//    void searchByName_ShouldReturnResults() throws Exception {
//        var expectedResults = List.of(
//                mock(RecipeDto.class),
//                mock(RecipeDto.class)
//        );
//        when(searchService.searchByName(any(SearchContainer.class))).thenReturn(expectedResults);
//
//        mockMvc.perform(get("/api/v1/search/name/chicken")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(expectedResults.size()))
//                .andExpect(jsonPath("$[0]").exists())
//                .andExpect(jsonPath("$[1]").exists());
//        verify(searchService, times(1)).searchByName(argThat(container ->
//                "chicken".equals(container.name())
//        ));
//    }
//
//    @Test
//    @WithMockUser(username = TEST_USER)
//    void searchByName_WithNoResults_ShouldReturnEmptyList() throws Exception {
//        when(searchService.searchByName(any(SearchContainer.class))).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(get("/api/v1/search/name/nonexistent")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(0));
//    }
//
//    @Test
//    @WithMockUser(username = TEST_USER)
//    void searchByIngredients_ShouldReturnResults() throws Exception {
//        var ingredients = Set.of("chicken", "rice");
//        var expectedResults = List.of(mock(RecipeDto.class), mock(RecipeDto.class));
//        when(searchService.searchByIngredients(any(SearchContainer.class))).thenReturn(expectedResults);
//
//        mockMvc.perform(get("/api/v1/search/ingredients")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(ingredients))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(expectedResults.size()))
//                .andExpect(jsonPath("$[0]").exists())
//                .andExpect(jsonPath("$[1]").exists());
//        verify(searchService).searchByIngredients(argThat(container ->
//                container.ingredientNames().equals(ingredients) &&
//                        container.name() == null
//        ));
//    }
//
//    @Test
//    @WithMockUser(username = TEST_USER)
//    void searchByIngredients_WithNullBody_ShouldReturnBadRequest() throws Exception {
//        mockMvc.perform(get("/api/v1/search/ingredients")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @WithMockUser(username = TEST_USER)
//    void searchByIngredientsWithFiltering_ShouldReturnFilteredResults() throws Exception {
//        var expectedResults = List.of(mock(RecipeDto.class));
//        when(searchService.searchByIngredientsWithFiltering(any(SearchContainer.class))).thenReturn(expectedResults);
//
//        mockMvc.perform(get("/api/v1/search/ingredients-with-filtering")
//                        .param("ingredientsName", "chicken", "rice")
//                        .param("maxMins4Cook", "30")
//                        .param("maxTotalMins", "60")
//                        .param("maxMins4Prep", "15")
//                        .param("minServings", "2")
//                        .param("maxServings", "4")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(expectedResults.size()));
//        verify(searchService).searchByIngredientsWithFiltering(argThat(container ->
//                container.ingredientNames().equals(Set.of("chicken", "rice")) &&
//                        container.maxMinsForCooking() == 30 &&
//                        container.maxTotalMinutes() == 60 &&
//                        container.maxMinsForPreparing() == 15 &&
//                        container.minServings() == 2 &&
//                        container.maxServings() == 4
//        ));
//    }
//
//    @Test
//    @WithMockUser(username = TEST_USER)
//    void searchByIngredientsWithFiltering_WithNullFilterParameters_ShouldHandleNulls() throws Exception {
//        var expectedResults = List.of(mock(RecipeDto.class));
//        when(searchService.searchByIngredientsWithFiltering(any(SearchContainer.class))).thenReturn(expectedResults);
//
//        mockMvc.perform(get("/api/v1/search/ingredients-with-filtering")
//                        .param("ingredientsName", "chicken", "rice")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(expectedResults.size()));
//        verify(searchService).searchByIngredientsWithFiltering(argThat(container ->
//                container.ingredientNames().equals(Set.of("chicken", "rice")) &&
//                        container.maxMinsForCooking() == null &&
//                        container.maxTotalMinutes() == null &&
//                        container.maxMinsForPreparing() == null &&
//                        container.minServings() == null &&
//                        container.maxServings() == null
//        ));
//    }
//
//    @Test
//    @WithMockUser(username = TEST_USER)
//    void searchByNameWithFiltering_ShouldReturnFilteredResults() throws Exception {
//        var expectedResults = List.of(mock(RecipeDto.class));
//        when(searchService.searchByNameWithFiltering(any(SearchContainer.class))).thenReturn(expectedResults);
//
//        mockMvc.perform(get("/api/v1/search/name-with-filtering")
//                        .param("name", "pasta")
//                        .param("maxMins4Cook", "30")
//                        .param("maxTotalMins", "60")
//                        .param("maxMins4Prep", "15")
//                        .param("minServings", "2")
//                        .param("maxServings", "4")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(expectedResults.size()));
//        verify(searchService).searchByNameWithFiltering(argThat(container ->
//                "pasta".equals(container.name()) &&
//                        container.maxMinsForCooking() == 30 &&
//                        container.maxTotalMinutes() == 60 &&
//                        container.maxMinsForPreparing() == 15 &&
//                        container.minServings() == 2 &&
//                        container.maxServings() == 4
//        ));
//    }
//
//    @Test
//    @WithMockUser(username = TEST_USER)
//    void searchByNameWithFiltering_WithNullFilterParameters_ShouldHandleNulls() throws Exception {
//        var expectedResults = List.of(mock(RecipeDto.class));
//        when(searchService.searchByNameWithFiltering(any(SearchContainer.class))).thenReturn(expectedResults);
//
//        mockMvc.perform(get("/api/v1/search/name-with-filtering")
//                        .param("name", "pasta")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(expectedResults.size()));
//        verify(searchService).searchByNameWithFiltering(argThat(container ->
//                "pasta".equals(container.name()) &&
//                        container.maxMinsForCooking() == null &&
//                        container.maxTotalMinutes() == null &&
//                        container.maxMinsForPreparing() == null &&
//                        container.minServings() == null &&
//                        container.maxServings() == null
//        ));
//    }
//}
