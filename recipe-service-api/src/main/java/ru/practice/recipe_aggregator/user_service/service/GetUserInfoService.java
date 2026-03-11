package ru.practice.recipe_aggregator.user_service.service;

import ru.practice.recipe_aggregator.user_service.model.User;

public interface GetUserInfoService {

    User getUserByName(String username);
}
