CREATE TABLE daily_sales (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id BIGINT        NOT NULL,
    data          DATE          NOT NULL,
    fatturato     NUMERIC(12,2) NOT NULL CHECK (fatturato >= 0),
    coperti       INTEGER       NOT NULL CHECK (coperti >= 0),
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT uq_daily_sales_restaurant_data UNIQUE (restaurant_id, data)
);
