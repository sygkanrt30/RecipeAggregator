package ru.practice.recipe_aggregator.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practice.recipe_aggregator.security.TokenCookieSessionAuthenticationStrategy;
import ru.practice.recipe_aggregator.user_service.service.SaveUserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
class RegistrationController {
//    private final SaveUserService userService;
//    private final TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy;
//    private final AuthenticationManager authenticationManager;
//
//    @PostMapping("/reg")
//    public ResponseEntity<String> doReg(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestParam("username")
//            @Pattern(regexp = "^[A-Za-zА-Яа-яЁё][A-Za-zА-Яа-яЁё0-9_]*$",
//                    message = "Incorrect username")
//            String username,
//
//            @RequestParam("password")
//            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" +
//                    "(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*[a-zA-Z].*$",
//                    message = "Incorrect password")
//            String password,
//
//            @RequestParam("email")
//            @Pattern(regexp = "^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$",
//                    message = "Incorrect email")
//            String email,
//
//            @RequestParam("_csrf") String csrfToken) {
//        try {
//            var storedToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//            if (storedToken == null || !storedToken.getToken().equals(csrfToken)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid CSRF token");
//            }
//            userService.save(username, password, email);
//            authenticate(request, response, username, password);
//            return ResponseEntity.ok("Registration successful");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
//        }
//    }
//
//    private void authenticate(HttpServletRequest request, HttpServletResponse response,
//                              String username, String password) {
//        try {
//            var authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(username, password)
//            );
//            tokenCookieSessionAuthenticationStrategy.onAuthentication(authentication, request, response);
//        } catch (AuthenticationException e) {
//            throw new RuntimeException("Authentication failed after registration", e);
//        }
//    }
}
