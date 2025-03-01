DO $$
BEGIN
    -- Insert Dishes
INSERT INTO dish (name, unit_price) VALUES
                                        ('Hot Dog', 15000),
                                        ('Burger', 20000),
                                        ('Pizza', 25000);

-- Insert Ingredients
INSERT INTO ingredient (name) VALUES
                                  ('Saucisse'),
                                  ('Pain'),
                                  ('Huile'),
                                  ('Oeuf');

-- Insert Prices for Ingredients
INSERT INTO price (amount, unit, date, id_ingredient) VALUES
                                                          (20, 'G', '2024-02-02', 1),  -- Saucisse
                                                          (1000, 'U', '2024-02-03', 2), -- Pain
                                                          (10000, 'L', '2024-02-03', 3), -- Huile
                                                          (1000, 'U', '2024-02-03', 4); -- Oeuf

-- Insert Dish-Ingredient Relations
INSERT INTO dish_ingredient (id_dish, id_ingredient, required_quantity, unit) VALUES
                                                                                  (1, 1, 100, 'G'),  -- Hot Dog needs 100g Saucisse
                                                                                  (1, 2, 1, 'U'),    -- Hot Dog needs 1 Pain
                                                                                  (1, 3, 0.15, 'L'), -- Hot Dog needs 0.15L Huile
                                                                                  (1, 4, 1, 'U'),    -- Hot Dog needs 1 Oeuf
                                                                                  (2, 2, 1, 'U'),    -- Burger needs 1 Pain
                                                                                  (3, 3, 0.2, 'L');  -- Pizza needs 0.2L Huile

-- Insert Price for Ingredient (Saucisse)
INSERT INTO price (amount, unit, date, id_ingredient) VALUES
    (30, 'G', '2024-02-03', 1);

-- Insert Stock Movements
INSERT INTO stock_move (id_ingredient, move_type, ingredient_quantity, unit, move_date) VALUES
                                                                                            (1, 'ENTRY', 10000.0, 'G', '2025-02-01 08:00:00'),-- saucisse
                                                                                            (2, 'ENTRY', 50.0, 'U', '2025-02-01 08:00:00'), -- pain
                                                                                            (3, 'ENTRY', 20.0, 'L', '2025-02-01 08:00:00'), -- huile
                                                                                            (4, 'ENTRY', 100.0, 'U', '2025-02-01 08:00:00');-- oeuf

INSERT INTO stock_move (id_ingredient, move_type, ingredient_quantity, unit, move_date) VALUES
                                                                                            (1, 'EXIT', 100.0, 'G', '2025-02-02 12:00:00'),  -- 100g de saucisse pour Hot Dog
                                                                                            (2, 'EXIT', 1.0, 'U', '2025-02-02 12:00:00'),   -- 1 pain pour Hot Dog
                                                                                            (3, 'EXIT', 0.15, 'L', '2025-02-02 12:00:00'),  -- 0.15L huile pour Hot Dog
                                                                                            (4, 'EXIT', 1.0, 'U', '2025-02-02 12:00:00'),   -- 1 oeuf pour Hot Dog
                                                                                            (2, 'EXIT', 1.0, 'U', '2025-02-02 13:00:00'),   -- 1 pain pour Burger
                                                                                            (3, 'EXIT', 0.2, 'L', '2025-02-02 14:00:00');   -- 0.2L huile pour Pizza
END $$;

DO $$
BEGIN
INSERT INTO ingredient (name) VALUES
                                  ('Sel'),
                                  ('Riz');

INSERT INTO price (id_ingredient, amount, unit, date)
VALUES
    ((SELECT id_ingredient FROM ingredient WHERE name = 'Sel'), 2.5, 'G', CURRENT_DATE),
    ((SELECT id_ingredient FROM ingredient WHERE name = 'Riz'), 3.5, 'G', CURRENT_DATE);

-- Add stock (ENTRY)
INSERT INTO stock_move (id_ingredient, move_type, ingredient_quantity, unit, move_date)
VALUES
    ((SELECT id_ingredient FROM ingredient WHERE name = 'Sel'), 'ENTRY', 500.0, 'G', '2025-02-01 08:00:00'),
    ((SELECT id_ingredient FROM ingredient WHERE name = 'Riz'), 'ENTRY', 1000.0, 'G', '2025-02-01 08:00:00');

-- Use stock (EXIT)
INSERT INTO stock_move (id_ingredient, move_type, ingredient_quantity, unit, move_date)
VALUES
    ((SELECT id_ingredient FROM ingredient WHERE name = 'Sel'), 'EXIT', 50.0, 'G', '2025-02-02 12:00:00'),
    ((SELECT id_ingredient FROM ingredient WHERE name = 'Riz'), 'EXIT', 200.0, 'G', '2025-02-02 12:00:00');

END $$;