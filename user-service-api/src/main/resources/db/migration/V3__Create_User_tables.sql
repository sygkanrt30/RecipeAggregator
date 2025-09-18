CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE favorite_recipe (
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, recipe_id),
    CONSTRAINT fk_favorite_recipe_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_favorite_recipe_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipe(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_app_user_username ON app_user(username);
CREATE INDEX idx_favorite_recipe_user_id ON favorite_recipe(user_id);
CREATE INDEX idx_favorite_recipe_recipe_id ON favorite_recipe(recipe_id);

COMMENT ON TABLE app_user IS 'Таблица пользователей';
COMMENT ON TABLE favorite_recipe IS 'Таблица рецептов, добавленных в избранное';