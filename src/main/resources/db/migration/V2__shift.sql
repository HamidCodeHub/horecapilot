CREATE TABLE shift (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    restaurant_id BIGINT      NOT NULL,
    employee_id   BIGINT      NOT NULL REFERENCES employee(id),
    data          DATE        NOT NULL,
    ora_inizio    TIME        NOT NULL,
    ora_fine      TIME        NOT NULL,
    ruolo         VARCHAR(60) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_shift_data     ON shift (data);
CREATE INDEX idx_shift_employee ON shift (employee_id);
