package ru.practice.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.practice.gateway.util.JwtUtil;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    private final int BEGIN_INDEX = 7;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            var requestPath = request.getPath().value();

            if (requestPath.startsWith("/auth")) {
                return chain.filter(exchange);
            }

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Authorization header is missing");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid authorization header");
            }

            String token = authHeader.substring(BEGIN_INDEX);
            if (!jwtUtil.validateToken(token)) {
                return onError(exchange, "Invalid or expired token");
            }

            var username = jwtUtil.extractUsername(token);
            var roles = jwtUtil.extractRoles(token);
            var modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", username)
                    .header("X-User-Roles", roles)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        var response = exchange.getResponse();
        var status = HttpStatus.UNAUTHORIZED;
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String responseBody = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                Instant.now(), status.value(), status.getReasonPhrase(), err
        );
        var buffer = response.bufferFactory()
                .wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
    }
}