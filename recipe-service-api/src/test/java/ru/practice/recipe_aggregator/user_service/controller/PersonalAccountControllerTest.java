package ru.practice.recipe_aggregator.user_service.controller;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.user_service.service.FavoriteRecipeService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonalAccountController.class)
class PersonalAccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoriteRecipeService favoriteRecipeService;

    private static final String TEST_USERNAME = "testuser";

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void addToFavorites_ShouldCallService() throws Exception {
        var recipeName = Instancio.create(String.class);

        mockMvc.perform(post("/api/v1/account/add-to-favorites")
                        .param("recipe_name", recipeName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(favoriteRecipeService).add2Favorites(TEST_USERNAME, recipeName);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void addToFavorites_WithoutCsrf_ShouldReturnForbidden() throws Exception {
        var recipeName = Instancio.create(String.class);

        mockMvc.perform(post("/api/v1/account/add-to-favorites")
                        .param("recipe_name", recipeName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(favoriteRecipeService, never()).add2Favorites(anyString(), anyString());
    }

    @Test
    void addToFavorites_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        var recipeName = Instancio.create(String.class);

        mockMvc.perform(post("/api/v1/account/add-to-favorites")
                        .param("recipe_name", recipeName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(favoriteRecipeService, never()).add2Favorites(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void removeFromFavorites_ShouldCallService() throws Exception {
        var recipeName = Instancio.create(String.class);

        mockMvc.perform(delete("/api/v1/account/remove-from-favorites")
                        .param("recipe_name", recipeName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(favoriteRecipeService).removeFromFavorites(TEST_USERNAME, recipeName);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void removeFromFavorites_WithoutCsrf_ShouldReturnForbidden() throws Exception {
        var recipeName = Instancio.create(String.class);

        mockMvc.perform(delete("/api/v1/account/remove-from-favorites")
                        .param("recipe_name", recipeName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(favoriteRecipeService, never()).removeFromFavorites(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void getFavorites_ShouldReturnFavoritesList() throws Exception {
        var favorites = Instancio.ofList(RecipeResponseDto.class).size(2).create();

        when(favoriteRecipeService.getFavorites(TEST_USERNAME)).thenReturn(favorites);

        mockMvc.perform(get("/api/v1/account/get-favorites")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(favoriteRecipeService).getFavorites(TEST_USERNAME);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void getFavorites_WhenNoFavorites_ShouldReturnEmptyList() throws Exception {
        when(favoriteRecipeService.getFavorites(TEST_USERNAME)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/account/get-favorites")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(favoriteRecipeService).getFavorites(TEST_USERNAME);
    }

    @Test
    void getFavorites_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/account/get-favorites")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(favoriteRecipeService, never()).getFavorites(anyString());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void addToFavorites_WithEmptyRecipeName_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/v1/account/add-to-favorites")
                        .param("recipe_name", "")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(favoriteRecipeService).add2Favorites(anyString(), anyString());
    }
}