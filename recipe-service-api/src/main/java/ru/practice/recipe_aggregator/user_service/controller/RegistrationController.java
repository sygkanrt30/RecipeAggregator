package ru.practice.recipe_aggregator.user_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practice.recipe_aggregator.user_service.model.dto.UserCredentials;
import ru.practice.recipe_aggregator.user_service.service.SaveUserService;



@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
class RegistrationController {

    private final SaveUserService userService;
    private final Authenticator authenticator;

    @PostMapping("/reg")
    public ResponseEntity<String> doReg(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody @Valid UserCredentials userCredentials) {
        var username = userCredentials.username();
        var password = userCredentials.password().getBytes();
        userService.save(username, password, userCredentials.email());
        authenticator.authenticateAndSetCookie(request, response, username, password);
        return ResponseEntity.ok("Registration successful");
    }
}
