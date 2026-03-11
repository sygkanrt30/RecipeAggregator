package ru.practice.recipe_aggregator.user_service.service;

public interface SaveUserService {

    void save(String username, byte[] password, String email);
}
