package ru.practice.recipe_aggregator.user_service.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practice.recipe_aggregator.user_service.service.SaveUserService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaveUserService userService;

    @MockitoBean
    private Authenticator authenticator;

    @Test
    @WithMockUser
    void doReg_ShouldReturnSuccess() throws Exception {
        var username = Instancio.create(String.class);
        var password = "Password123!";
        var email = "test@example.com";

        mockMvc.perform(post("/api/v1/auth/reg")
                        .param("username", username)
                        .param("password", password)
                        .param("email", email)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration successful"));

        verify(userService).save(username, password, email);
        verify(authenticator).authenticateAndSetCookie(
                any(HttpServletRequest.class), any(HttpServletResponse.class), eq(username), eq(password));
    }

    @Test
    @WithMockUser
    void doReg_WhenServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        var username = Instancio.create(String.class);
        var password = "Password123!";
        var email = "test@example.com";
        var errorMessage = "User already exists";

        doThrow(new RuntimeException(errorMessage))
                .when(userService).save(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/v1/auth/reg")
                        .param("username", username)
                        .param("password", password)
                        .param("email", email)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Registration failed: " + errorMessage));

        verify(authenticator, never()).authenticateAndSetCookie(any(), any(), any(), any());
    }

    @Test
    @WithMockUser
    void doReg_WithMissingUsername_ShouldReturnBadRequest() throws Exception {
        var password = "Password123!";
        var email = "test@example.com";

        mockMvc.perform(post("/api/v1/auth/reg")
                        .param("password", password)
                        .param("email", email)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(anyString(), anyString(), anyString());
        verify(authenticator, never()).authenticateAndSetCookie(any(), any(), any(), any());
    }

    @Test
    @WithMockUser
    void doReg_WithMissingPassword_ShouldReturnBadRequest() throws Exception {
        var username = Instancio.create(String.class);
        var email = "test@example.com";

        mockMvc.perform(post("/api/v1/auth/reg")
                        .param("username", username)
                        .param("email", email)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(anyString(), anyString(), anyString());
        verify(authenticator, never()).authenticateAndSetCookie(any(), any(), any(), any());
    }

    @Test
    @WithMockUser
    void doReg_WithMissingEmail_ShouldReturnBadRequest() throws Exception {
        var username = Instancio.create(String.class);
        var password = "Password123!";

        mockMvc.perform(post("/api/v1/auth/reg")
                        .param("username", username)
                        .param("password", password)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(anyString(), anyString(), anyString());
        verify(authenticator, never()).authenticateAndSetCookie(any(), any(), any(), any());
    }
}
