DO $$
BEGIN
CREATE TABLE dish (
                      id_dish SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      unit_price INT NOT NULL
);

CREATE TABLE ingredient (
                            id_ingredient SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL
);

CREATE TABLE price (
                       id SERIAL PRIMARY KEY,
                       id_ingredient INT NOT NULL,
                       amount decimal(10, 2) NOT NULL,
                       unit VARCHAR(50) NOT NULL,
                       date DATE NOT NULL,
                       CONSTRAINT unique_ingredient_date UNIQUE (id_ingredient, date),
                       FOREIGN KEY (id_ingredient) REFERENCES ingredient(id_ingredient) ON DELETE CASCADE
);

CREATE TABLE dish_ingredient (
                                 id_dish INT,
                                 id_ingredient INT,
                                 required_quantity DECIMAL NOT NULL,
                                 unit CHAR(1) CHECK (unit IN ('G', 'L', 'U')),
                                 PRIMARY KEY (id_dish, id_ingredient),
                                 FOREIGN KEY (id_dish) REFERENCES dish(id_dish),
                                 FOREIGN KEY (id_ingredient) REFERENCES ingredient(id_ingredient)
);

CREATE TABLE stock_move (
                            id_move SERIAL PRIMARY KEY,
                            move_type VARCHAR(6) CHECK (move_type IN ('ENTRY', 'EXIT')),
                            ingredient_quantity DECIMAL NOT NULL,
                            unit CHAR(1) CHECK (unit IN ('G', 'L', 'U')),
                            move_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                            id_ingredient INT NOT NULL,
                            CONSTRAINT unique_move UNIQUE (id_ingredient, move_date, move_type, ingredient_quantity),
                            FOREIGN KEY (id_ingredient) REFERENCES ingredient(id_ingredient)
);
END $$;
