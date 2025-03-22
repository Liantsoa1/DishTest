
CREATE TABLE order_status (
                              id_order_status SERIAL PRIMARY KEY,
                              id_order INT NOT NULL,
                              status status_enum NOT NULL,
                              change_date TIMESTAMP NOT NULL DEFAULT NOW(),
                              FOREIGN KEY (id_order) REFERENCES "order"(id_order) ON DELETE CASCADE
);