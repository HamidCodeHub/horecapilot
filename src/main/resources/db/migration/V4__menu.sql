CREATE TABLE ingredient (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id  BIGINT        NOT NULL,
    nome           VARCHAR(150)  NOT NULL,
    unita          VARCHAR(20)   NOT NULL,
    costo_unitario NUMERIC(12,4) NOT NULL CHECK (costo_unitario >= 0),
    attivo         BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE TABLE menu_item (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id  BIGINT        NOT NULL,
    nome           VARCHAR(150)  NOT NULL,
    prezzo_vendita NUMERIC(10,2) NOT NULL CHECK (prezzo_vendita >= 0),
    categoria      VARCHAR(60),
    attivo         BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE TABLE recipe_line (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    menu_item_id  BIGINT        NOT NULL REFERENCES menu_item(id),
    ingredient_id BIGINT        NOT NULL REFERENCES ingredient(id),
    quantita      NUMERIC(12,4) NOT NULL CHECK (quantita > 0),
    CONSTRAINT uq_recipe_line UNIQUE (menu_item_id, ingredient_id)
);

CREATE INDEX idx_recipe_line_menu_item  ON recipe_line (menu_item_id);
CREATE INDEX idx_recipe_line_ingredient ON recipe_line (ingredient_id);
