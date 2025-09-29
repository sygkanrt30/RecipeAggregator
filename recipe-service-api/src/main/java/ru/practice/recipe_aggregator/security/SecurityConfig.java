package ru.practice.recipe_aggregator.security;

import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import ru.practice.recipe_aggregator.user_service.repository.UserRepository;
import ru.practice.recipe_aggregator.user_service.token.TokenCookieJweStringDeserializer;
import ru.practice.recipe_aggregator.user_service.token.TokenCookieJweStringSerializer;


@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy,
            TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer) throws Exception {
        return http.httpBasic(Customizer.withDefaults())
                .with(tokenCookieAuthenticationConfigurer, Customizer.withDefaults())
                .csrf(csrf -> csrf.csrfTokenRepository(new CookieCsrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .sessionAuthenticationStrategy((authentication, request, response) -> {
                        }))
                .formLogin(formLogin ->
                        //для ручных тестов
                        formLogin.defaultSuccessUrl("/api/v1/search/search-by-name/chicken", false))
                .addFilterAfter(new GetCsrfTokenFilter(), ExceptionTranslationFilter.class)
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/auth/reg").not().fullyAuthenticated()
                                .anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .sessionAuthenticationStrategy(tokenCookieSessionAuthenticationStrategy))
                .build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenCookieJweStringSerializer tokenCookieJweStringSerializer(
            @Value("${jwt.cookie-token-key}") String cookieTokenKey
    ) throws Exception {
        return new TokenCookieJweStringSerializer(new DirectEncrypter(
                OctetSequenceKey.parse(cookieTokenKey)
        ));
    }

    @Bean
    public TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer(
            @Value("${jwt.cookie-token-key}") String cookieTokenKey,
            JdbcTemplate jdbcTemplate,
            UserRepository userRepository
    ) throws Exception {
        return new TokenCookieAuthenticationConfigurer()
                .tokenCookieStringDeserializer(new TokenCookieJweStringDeserializer(
                        new DirectDecrypter(
                                OctetSequenceKey.parse(cookieTokenKey)
                        )
                ))
                .jdbcTemplate(jdbcTemplate)
                .userRepository(userRepository);
    }

    @Bean
    public TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy(
            TokenCookieJweStringSerializer tokenCookieJweStringSerializer) {
        var tokenCookieSessionAuthenticationStrategy = new TokenCookieSessionAuthenticationStrategy();
        tokenCookieSessionAuthenticationStrategy.setTokenStringSerializer(tokenCookieJweStringSerializer);
        return tokenCookieSessionAuthenticationStrategy;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}


