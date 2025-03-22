
CREATE TABLE dish_order_status (
                                   id_dish_order_status SERIAL PRIMARY KEY,
                                   id_dish_order INT NOT NULL,
                                   status status_enum NOT NULL,
                                   change_date TIMESTAMP NOT NULL DEFAULT NOW(),
                                   FOREIGN KEY (id_dish_order) REFERENCES dish_order(id_dish_order) ON DELETE CASCADE
);