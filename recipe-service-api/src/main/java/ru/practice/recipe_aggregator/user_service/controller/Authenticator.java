package ru.practice.recipe_aggregator.user_service.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.practice.recipe_aggregator.security.TokenCookieSessionAuthenticationStrategy;

@RequiredArgsConstructor
@Component
@Slf4j
final class Authenticator {
    private final TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy;
    private final AuthenticationManager authenticationManager;

    public void authenticateAndSetCookie(HttpServletRequest request, HttpServletResponse response,
                                         String username, String password) {
        log.debug("trying to authenticate user with username ({}) after registration", username);
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            tokenCookieSessionAuthenticationStrategy.onAuthentication(authentication, request, response);
        } catch (AuthenticationException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Authentication failed after registration", e);
        }
        log.debug("successfully authenticated user with username ({}) after registration", username);
    }
}
