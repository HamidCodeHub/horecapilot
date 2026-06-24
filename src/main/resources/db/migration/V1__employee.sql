CREATE TABLE employee (
    id                       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id            BIGINT        NOT NULL,
    nome                     VARCHAR(150)  NOT NULL,
    ruolo                    VARCHAR(60)   NOT NULL,
    costo_orario_aziendale   NUMERIC(10,4) NOT NULL CHECK (costo_orario_aziendale >= 0),
    attivo                   BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at               TIMESTAMPTZ   NOT NULL DEFAULT now()
);