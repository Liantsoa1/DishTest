  CREATE TABLE dish_order (
        id_dish_order SERIAL PRIMARY KEY,
        id_order INT NOT NULL,
        id_dish INT NOT NULL,
        quantity INT NOT NULL CHECK (quantity > 0),
        current_status status_enum NOT NULL DEFAULT 'CREATED',
        FOREIGN KEY (id_order) REFERENCES "order"(id_order) ON DELETE CASCADE,
        FOREIGN KEY (id_dish) REFERENCES dish(id_dish) ON DELETE CASCADE
    );