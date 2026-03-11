package ru.practice.recipe_aggregator.recipe_management.search_service.search;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterOperator;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterService;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception.InvalidConditionException;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher.IngredientsSearcher;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher.NameSearcher;
import ru.practice.shared.dto.RecipeDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private FilterService filterService;
    @Mock
    private RecipeMapper mapper;
    @Mock
    private NameSearcher nameSearcher;
    @Mock
    private IngredientsSearcher ingredientsSearcher;
    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void searchByName_ShouldReturnMappedResults() {
        String name = "pizza";
        var recipeDoc1 = mock(RecipeDoc.class);
        var recipeDoc2 = mock(RecipeDoc.class);
        var recipeDto1 = mock(RecipeDto.class);
        var recipeDto2 = mock(RecipeDto.class);

        when(nameSearcher.search(name)).thenReturn(Set.of(recipeDoc1, recipeDoc2));
        when(mapper.toRecipeDto(recipeDoc1)).thenReturn(recipeDto1);
        when(mapper.toRecipeDto(recipeDoc2)).thenReturn(recipeDto2);

        var result = searchService.searchByName(name);

        assertEquals(2, result.size());
        assertTrue(result.contains(recipeDto1));
        assertTrue(result.contains(recipeDto2));
    }

    @Test
    void searchByIngredients_ShouldReturnMappedResults() {
        var ingredients = Set.of("flour", "sugar");
        var recipeDoc1 = mock(RecipeDoc.class);
        var recipeDoc2 = mock(RecipeDoc.class);
        var recipeDto1 = mock(RecipeDto.class);
        var recipeDto2 = mock(RecipeDto.class);

        when(ingredientsSearcher.search(ingredients)).thenReturn(Set.of(recipeDoc1, recipeDoc2));
        when(mapper.toRecipeDto(recipeDoc1)).thenReturn(recipeDto1);
        when(mapper.toRecipeDto(recipeDoc2)).thenReturn(recipeDto2);

        var result = searchService.searchByIngredients(ingredients);

        assertEquals(2, result.size());
        assertTrue(result.contains(recipeDto1));
        assertTrue(result.contains(recipeDto2));
    }

    @Test
    void searchByName_WithEmptyResult_ShouldReturnEmptyList() {
        String name = "nonexistent";
        when(nameSearcher.search(name)).thenReturn(Set.of());

        var result = searchService.searchByName(name);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchByIngredients_WithEmptyResult_ShouldReturnEmptyList() {
        var ingredients = Set.of("nonexistent");
        when(ingredientsSearcher.search(ingredients)).thenReturn(Set.of());

        var result = searchService.searchByIngredients(ingredients);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchWithFiltering_WhenNameAndIngredientsNotEmptyWithIntersection_ReturnsFilteredResults() {
        var container = SearchContainer.builder()
                .name("test")
                .ingredientNames(Set.of("ingredient1", "ingredient2"))
                .build();

        var recipeDoc1 = Instancio.create(RecipeDoc.class);
        var recipeDoc2 = Instancio.create(RecipeDoc.class);
        var recipeDocsByName = new HashSet<>(Set.of(recipeDoc1, recipeDoc2));
        var recipeDocsByIngredients = Set.of(recipeDoc1, recipeDoc2);

        when(nameSearcher.search("test")).thenReturn(recipeDocsByName);
        when(ingredientsSearcher.search(Set.of("ingredient1", "ingredient2"))).thenReturn(recipeDocsByIngredients);

        var recipeDto = Instancio.create(RecipeDto.class);
        when(mapper.toRecipeDto(recipeDoc1)).thenReturn(recipeDto);
        when(mapper.toRecipeDto(recipeDoc2)).thenReturn(recipeDto);
        when(filterService.processWithFilterChain(any(), eq(container))).thenReturn(List.of(recipeDto));

        var result = searchService.searchWithFiltering(container);

        assertFalse(result.isEmpty());
        verify(filterService).processWithFilterChain(any(), eq(container));
    }

    @Test
    void searchWithFiltering_WhenNameAndIngredientsNotEmptyWithoutIntersection_ReturnsEmptyList() {
        var container = SearchContainer.builder()
                .name("test")
                .ingredientNames(Set.of("ingredient1", "ingredient2"))
                .build();

        var recipeDoc1 = Instancio.create(RecipeDoc.class);
        var recipeDoc2 = Instancio.create(RecipeDoc.class);
        var recipeDocsByName = new HashSet<>(Set.of(recipeDoc1));
        var recipeDocsByIngredients = Set.of(recipeDoc2);

        when(nameSearcher.search("test")).thenReturn(recipeDocsByName);
        when(ingredientsSearcher.search(Set.of("ingredient1", "ingredient2"))).thenReturn(recipeDocsByIngredients);

        var result = searchService.searchWithFiltering(container);

        assertTrue(result.isEmpty());
        verify(filterService, never()).processWithFilterChain(any(), any());
    }

    @Test
    void searchWithFiltering_WhenOnlyNameNotEmpty_ReturnsNameSearchResultsWithFiltering() {
        var container = SearchContainer.builder()
                .name("test")
                .build();

        var recipeDoc1 = Instancio.create(RecipeDoc.class);
        var recipeDoc2 = Instancio.create(RecipeDoc.class);
        var recipeDocs = Set.of(recipeDoc1, recipeDoc2);

        var recipeDto1 = Instancio.create(RecipeDto.class);
        var recipeDto2 = Instancio.create(RecipeDto.class);
        var expectedDtos = List.of(recipeDto1, recipeDto2);

        when(nameSearcher.search("test")).thenReturn(recipeDocs);
        when(mapper.toRecipeDto(recipeDoc1)).thenReturn(recipeDto1);
        when(mapper.toRecipeDto(recipeDoc2)).thenReturn(recipeDto2);
        when(filterService.processWithFilterChain(any(), eq(container))).thenReturn(expectedDtos);

        var result = searchService.searchWithFiltering(container);

        assertEquals(expectedDtos, result);
        verify(ingredientsSearcher, never()).search(any());
        verify(filterService).processWithFilterChain(any(), eq(container));
    }

    @Test
    void searchWithFiltering_WhenOnlyIngredientsNotEmpty_ReturnsIngredientsSearchResultsWithFiltering() {
        var container = SearchContainer.builder()
                .ingredientNames(Set.of("ingredient1", "ingredient2"))
                .build();

        var recipeDoc1 = Instancio.create(RecipeDoc.class);
        var recipeDoc2 = Instancio.create(RecipeDoc.class);
        var recipeDocs = Set.of(recipeDoc1, recipeDoc2);

        var recipeDto1 = Instancio.create(RecipeDto.class);
        var recipeDto2 = Instancio.create(RecipeDto.class);
        var expectedDtos = List.of(recipeDto1, recipeDto2);

        when(ingredientsSearcher.search(Set.of("ingredient1", "ingredient2"))).thenReturn(recipeDocs);
        when(mapper.toRecipeDto(recipeDoc1)).thenReturn(recipeDto1);
        when(mapper.toRecipeDto(recipeDoc2)).thenReturn(recipeDto2);
        when(filterService.processWithFilterChain(any(), eq(container))).thenReturn(expectedDtos);

        var result = searchService.searchWithFiltering(container);

        assertEquals(expectedDtos, result);
        verify(nameSearcher, never()).search(any());
        verify(ingredientsSearcher).search(Set.of("ingredient1", "ingredient2"));
        verify(filterService).processWithFilterChain(any(), eq(container));
    }

    @Test
    void searchWithFiltering_WhenContainerHasFilterConditions_AppliesFilterChain() {
        var container = SearchContainer.builder()
                .name("test")
                .cookingTimeCondition(new FilterCondition("cookingTime", FilterOperator.LT, 30))
                .servingsCondition(new FilterCondition("servings", FilterOperator.GTE, 4))
                .build();

        var recipeDoc1 = Instancio.create(RecipeDoc.class);
        var recipeDoc2 = Instancio.create(RecipeDoc.class);
        var recipeDocs = Set.of(recipeDoc1, recipeDoc2);

        var recipeDto = Instancio.create(RecipeDto.class);
        var expectedDtos = List.of(recipeDto);

        when(nameSearcher.search("test")).thenReturn(recipeDocs);
        when(mapper.toRecipeDto(recipeDoc1)).thenReturn(recipeDto);
        when(mapper.toRecipeDto(recipeDoc2)).thenReturn(recipeDto);
        when(filterService.processWithFilterChain(any(), eq(container))).thenReturn(expectedDtos);

        var result = searchService.searchWithFiltering(container);

        assertEquals(expectedDtos, result);
        verify(filterService).processWithFilterChain(any(), eq(container));
    }

    @Test
    void searchWithFiltering_WhenBothNameAndIngredientsEmpty_ThrowsException() {
        var container = SearchContainer.builder().build();

        assertThrows(InvalidConditionException.class, () ->
                searchService.searchWithFiltering(container)
        );

        verify(nameSearcher, never()).search(any());
        verify(ingredientsSearcher, never()).search(any());
        verify(filterService, never()).processWithFilterChain(any(), any());
    }

    @Test
    void searchWithFiltering_WhenNameEmptyAndIngredientsEmptySet_ThrowsException() {
        var container = SearchContainer.builder()
                .name("")
                .ingredientNames(Set.of())
                .build();

        assertThrows(InvalidConditionException.class, () ->
                searchService.searchWithFiltering(container)
        );
    }

}