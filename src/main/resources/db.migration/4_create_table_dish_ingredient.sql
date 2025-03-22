CREATE TABLE dish_ingredient (
                                 id_dish INT,
                                 id_ingredient INT,
                                 required_quantity DECIMAL NOT NULL,
                                 unit CHAR(1) CHECK (unit IN ('G', 'L', 'U')),
                                 PRIMARY KEY (id_dish, id_ingredient),
                                 FOREIGN KEY (id_dish) REFERENCES dish(id_dish),
                                 FOREIGN KEY (id_ingredient) REFERENCES ingredient(id_ingredient)
);