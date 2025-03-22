CREATE TABLE "order" (
                         id_order SERIAL PRIMARY KEY,
                         reference UUID DEFAULT gen_random_uuid() UNIQUE NOT NULL,
                         order_date TIMESTAMP NOT NULL DEFAULT NOW(),
                         current_status status_enum NOT NULL DEFAULT 'CREATED'
);
