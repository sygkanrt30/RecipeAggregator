package ru.practice.recipe_aggregator.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practice.recipe_aggregator.security.TokenCookieSessionAuthenticationStrategy;
import ru.practice.recipe_aggregator.user_service.service.SaveUserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class RegistrationController {
    private final SaveUserService userService;
    private final TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy;

    @PostMapping("/reg")
    public void doReg(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("username")
            @Pattern(regexp = "^[A-Za-zА-Яа-яЁё][A-Za-zА-Яа-яЁё0-9_]*$", message = "Incorrect username")
            String username,
                      @RequestParam("password")
            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" +
                    "(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*[a-zA-Z].*$", message = "Incorrect password")
            String password,
                      @RequestParam("email")
            @Pattern(regexp = "^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$", message = "Incorrect email")
            String email) {
        userService.save(username, password, email);
        authenticate(request, response, username, password);
    }

    private void authenticate(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        var authToken = new UsernamePasswordAuthenticationToken(username, password);
        tokenCookieSessionAuthenticationStrategy.onAuthentication(authToken, request, response);
    }
}
