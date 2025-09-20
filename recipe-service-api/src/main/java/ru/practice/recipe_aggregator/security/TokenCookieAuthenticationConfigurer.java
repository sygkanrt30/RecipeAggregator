package ru.practice.recipe_aggregator.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;
import ru.practice.recipe_aggregator.user_service.model.User;
import ru.practice.recipe_aggregator.user_service.repository.UserRepository;
import ru.practice.recipe_aggregator.user_service.token.Token;
import ru.practice.recipe_aggregator.user_service.token.TokenAuthenticationUserDetailsService;

import java.util.Date;
import java.util.function.Function;

@Setter
@Accessors(chain = true, fluent = true)
public class TokenCookieAuthenticationConfigurer
        extends AbstractHttpConfigurer<TokenCookieAuthenticationConfigurer, HttpSecurity> {
    private Function<String, Token> tokenCookieStringDeserializer;
    private JdbcTemplate jdbcTemplate;
    private UserRepository userRepository;

    @Override
    public void init(HttpSecurity builder) throws Exception {
        builder.logout(logout -> logout.addLogoutHandler(
                        new CookieClearingLogoutHandler(CookieName.HOST_AUTH_TOKEN.name()))
                .addLogoutHandler((request, response, authentication) -> {
                    if (authentication != null &&
                            authentication.getPrincipal() instanceof User user) {
                        jdbcTemplate.update("insert into deactivated_token (id, keep_until) values (?, ?)",
                                user.getToken().id(), Date.from(user.getToken().expiresAt()));
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    }
                }));
    }

    @Override
    public void configure(HttpSecurity builder) {
        var cookieAuthenticationFilter = new AuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                new TokenCookieAuthenticationConverter(tokenCookieStringDeserializer));
        cookieAuthenticationFilter.setSuccessHandler(
                (request, response, authentication) -> {
                });
        cookieAuthenticationFilter.setFailureHandler(
                new AuthenticationEntryPointFailureHandler(
                        new Http403ForbiddenEntryPoint()
                )
        );

        var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(
                new TokenAuthenticationUserDetailsService(userRepository));

        builder.addFilterAfter(cookieAuthenticationFilter, CsrfFilter.class)
                .authenticationProvider(authenticationProvider);
    }
}
