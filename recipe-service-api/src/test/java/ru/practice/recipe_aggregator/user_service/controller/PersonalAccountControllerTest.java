package ru.practice.recipe_aggregator.user_service.controller;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practice.recipe_aggregator.translator.TranslatorUtil;
import ru.practice.recipe_aggregator.user_service.service.FavoriteRecipeService;
import ru.practice.shared.dto.RecipeDto;

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

    @MockitoBean
    private TranslatorUtil translator;

    private static final String TEST_USERNAME = "testuser";

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void addToFavorites_ShouldCallService() throws Exception {
        var recipeName = Instancio.create(String.class);
        var translatedName = "translated_" + recipeName;

        when(translator.translateTextDependingOnWebsiteLanguage(recipeName)).thenReturn(translatedName);

        mockMvc.perform(post("/api/v1/account/favorite")
                        .param("recipe_name", recipeName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(favoriteRecipeService).add2Favorites(TEST_USERNAME, translatedName);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void addToFavorites_WithoutCsrf_ShouldReturnForbidden() throws Exception {
        var recipeName = Instancio.create(String.class);

        mockMvc.perform(post("/api/v1/account/favorite")
                        .param("recipe_name", recipeName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(favoriteRecipeService, never()).add2Favorites(anyString(), anyString());
    }

    @Test
    void addToFavorites_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        var recipeName = Instancio.create(String.class);

        mockMvc.perform(post("/api/v1/account/favorite")
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
        var translatedName = "translated_" + recipeName;

        when(translator.translateTextDependingOnWebsiteLanguage(recipeName)).thenReturn(translatedName);

        mockMvc.perform(delete("/api/v1/account/favorite")
                        .param("recipe_name", recipeName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(translator).translateTextDependingOnWebsiteLanguage(recipeName);
        verify(favoriteRecipeService).removeFromFavorites(TEST_USERNAME, translatedName);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void removeFromFavorites_WithoutCsrf_ShouldReturnForbidden() throws Exception {
        var recipeName = Instancio.create(String.class);

        mockMvc.perform(delete("/api/v1/account/favorite")
                        .param("recipe_name", recipeName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(favoriteRecipeService, never()).removeFromFavorites(anyString(), anyString());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void getFavorites_ShouldReturnFavoritesList() throws Exception {
        var favorites = Instancio.ofList(RecipeDto.class).size(2).create();
        var translatedFavorites = Instancio.ofList(RecipeDto.class).size(2).create();

        when(favoriteRecipeService.getFavorites(TEST_USERNAME, 0, 15)).thenReturn(favorites);
        when(translator.translateDtoDependingOnWebsiteLanguage(favorites)).thenReturn(translatedFavorites);

        mockMvc.perform(get("/api/v1/account/favorite")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(favoriteRecipeService).getFavorites(TEST_USERNAME, 0, 15);
        verify(translator).translateDtoDependingOnWebsiteLanguage(favorites);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void getFavorites_WithCustomPagination_ShouldReturnFavoritesList() throws Exception {
        var favorites = Instancio.ofList(RecipeDto.class).size(5).create();
        var translatedFavorites = Instancio.ofList(RecipeDto.class).size(5).create();

        when(favoriteRecipeService.getFavorites(TEST_USERNAME, 2, 10)).thenReturn(favorites);
        when(translator.translateDtoDependingOnWebsiteLanguage(favorites)).thenReturn(translatedFavorites);

        mockMvc.perform(get("/api/v1/account/favorite")
                        .param("page", "2")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));

        verify(favoriteRecipeService).getFavorites(TEST_USERNAME, 2, 10);
        verify(translator).translateDtoDependingOnWebsiteLanguage(favorites);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void getFavorites_WhenNoFavorites_ShouldReturnEmptyList() throws Exception {
        List<RecipeDto> emptyList = List.of();

        when(favoriteRecipeService.getFavorites(TEST_USERNAME, 0, 15)).thenReturn(emptyList);
        when(translator.translateDtoDependingOnWebsiteLanguage(emptyList)).thenReturn(emptyList);

        mockMvc.perform(get("/api/v1/account/favorite")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(favoriteRecipeService).getFavorites(TEST_USERNAME, 0, 15);
        verify(translator).translateDtoDependingOnWebsiteLanguage(emptyList);
    }

    @Test
    void getFavorites_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/account/favorite")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(favoriteRecipeService, never()).getFavorites(anyString(), anyInt(), anyInt());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void addToFavorites_WithEmptyRecipeName_ShouldReturnOk() throws Exception {
        var emptyRecipeName = "";
        var translatedName = "translated_empty";

        when(translator.translateTextDependingOnWebsiteLanguage(emptyRecipeName)).thenReturn(translatedName);

        mockMvc.perform(post("/api/v1/account/favorite")
                        .param("recipe_name", emptyRecipeName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(translator).translateTextDependingOnWebsiteLanguage(emptyRecipeName);
        verify(favoriteRecipeService).add2Favorites(TEST_USERNAME, translatedName);
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void removeFromFavorites_WithEmptyRecipeName_ShouldReturnOk() throws Exception {
        var emptyRecipeName = "";
        var translatedName = "translated_empty";

        when(translator.translateTextDependingOnWebsiteLanguage(emptyRecipeName)).thenReturn(translatedName);

        mockMvc.perform(delete("/api/v1/account/favorite")
                        .param("recipe_name", emptyRecipeName)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(translator).translateTextDependingOnWebsiteLanguage(emptyRecipeName);
        verify(favoriteRecipeService).removeFromFavorites(TEST_USERNAME, translatedName);
    }
}