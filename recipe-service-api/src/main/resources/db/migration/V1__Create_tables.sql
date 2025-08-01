CREATE TABLE recipe (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    servings INTEGER NOT NULL,
    description VARCHAR(600) NOT NULL,
    direction VARCHAR(3000) NOT NULL,
    mins_for_preparing INTEGER,
    mins_for_cooking INTEGER NOT NULL,
    additional_mins INTEGER,
    total_mins INTEGER,
    created_at TIMESTAMP
);

CREATE TABLE ingredient (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity VARCHAR(20) NOT NULL,
    unit VARCHAR(30),
    recipe_id BIGINT NOT NULL,
    CONSTRAINT fk_ingredient_recipe FOREIGN KEY (recipe_id) REFERENCES recipe (id) ON DELETE CASCADE
);

CREATE INDEX idx_ingredient_recipe_id ON ingredient (recipe_id);
CREATE INDEX idx_recipe_name ON recipe(name);

COMMENT ON TABLE recipe IS 'Таблица рецептов';
COMMENT ON TABLE ingredient IS 'Таблица ингредиентов для рецептов';