package ru.practice.recipe_aggregator.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practice.recipe_aggregator.user_service.service.SaveUserService;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Validated
class RegistrationController {
    private final SaveUserService userService;
    private final Authenticator authenticator;

    @PostMapping("/reg")
    public ResponseEntity<String> doReg(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("username")
            @Pattern(regexp = "^[A-Za-zА-Яа-яЁё][A-Za-zА-Яа-яЁё0-9_]*$",
                    message = "Incorrect username")
            String username,

            @RequestParam("password")
            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" +
                    "(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*[a-zA-Z].*$",
                    message = "Incorrect password")
            String password,

            @RequestParam("email")
            @Pattern(regexp = "^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$",
                    message = "Incorrect email")
            String email) {
        try {
            userService.save(username, password, email);
            authenticator.authenticateAndSetCookie(request, response, username, password);
            return ResponseEntity.ok("Registration successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }
}
