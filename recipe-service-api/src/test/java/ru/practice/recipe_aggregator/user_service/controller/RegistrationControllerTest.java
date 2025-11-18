package ru.practice.recipe_aggregator.user_service.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practice.recipe_aggregator.user_service.exception.RegistrationException;
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
        var username = "ValidUser123";
        var password = "Password123!";
        var email = "test@example.com";
        mockMvc.perform(post("/api/v1/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "%s",
                                    "email": "%s"
                                }
                                """.formatted(username, password, email))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration successful"));

        verify(userService).save(username, password.getBytes(), email);
        verify(authenticator).authenticateAndSetCookie(
                any(HttpServletRequest.class), any(HttpServletResponse.class), eq(username), eq(password.getBytes()));
    }

    @Test
    @WithMockUser
    void doReg_WhenServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        var username = "ValidUser123";
        var password = "Password123!";
        var email = "test@example.com";
        var errorMessage = "User already exists";

        doThrow(new RegistrationException(errorMessage))
                .when(userService).save(anyString(), any(), anyString());

        mockMvc.perform(post("/api/v1/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "%s",
                                    "email": "%s"
                                }
                                """.formatted(username, password, email))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(content().string(errorMessage));

        verify(authenticator, never()).authenticateAndSetCookie(any(), any(), any(), any());
    }

    @Test
    @WithMockUser
    void doReg_WithMissingUsername_ShouldReturnBadRequest() throws Exception {
        var password = "Password123!";
        var email = "test@example.com";

        mockMvc.perform(post("/api/v1/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "password": "%s",
                                    "email": "%s"
                                }
                                """.formatted(password, email))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(anyString(), any(), anyString());
        verify(authenticator, never()).authenticateAndSetCookie(any(), any(), any(), any());
    }

    @Test
    @WithMockUser
    void doReg_WithMissingPassword_ShouldReturnBadRequest() throws Exception {
        var username = "ValidUser123";
        var email = "test@example.com";

        mockMvc.perform(post("/api/v1/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "email": "%s"
                                }
                                """.formatted(username, email))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(anyString(), any(), anyString());
        verify(authenticator, never()).authenticateAndSetCookie(any(), any(), any(), any());
    }

    @Test
    @WithMockUser
    void doReg_WithMissingEmail_ShouldReturnBadRequest() throws Exception {
        var username = "ValidUser123";
        var password = "Password123!";

        mockMvc.perform(post("/api/v1/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "%s"
                                }
                                """.formatted(username, password))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(anyString(), any(), anyString());
        verify(authenticator, never()).authenticateAndSetCookie(any(), any(), any(), any());
    }
}
