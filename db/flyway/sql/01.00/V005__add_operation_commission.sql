ALTER TABLE lab2.operations
    ADD COLUMN commission NUMERIC(10, 2) NOT NULL DEFAULT 0;

ALTER TABLE lab2.operations_aud
    ADD COLUMN commission NUMERIC(10, 2) NOT NULL DEFAULT 0;