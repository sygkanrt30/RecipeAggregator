package ru.practice.recipe_aggregator.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.practice.recipe_aggregator.user_service.token.Token;

import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
class TokenCookieAuthenticationConverter implements AuthenticationConverter {
    private final Function<String, Token> tokenCookieStringDeserializer;

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Stream.of(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(CookieName.HOST_AUTH_TOKEN.name()))
                    .findFirst()
                    .map(cookie -> {
                        var token = tokenCookieStringDeserializer.apply(cookie.getValue());
                        return new PreAuthenticatedAuthenticationToken(token, cookie.getValue());
                    })
                    .orElse(null);
        }
        return null;
    }
}