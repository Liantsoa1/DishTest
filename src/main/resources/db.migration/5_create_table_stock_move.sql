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
