-- Перевод хранится одной записью: account_id - счёт списания, destination_id - счёт зачисления.
ALTER TABLE lab2.operations
    ADD COLUMN destination_id UUID;

ALTER TABLE lab2.operations
    ADD CONSTRAINT fk_op_dest_acc_id FOREIGN KEY (destination_id) REFERENCES lab2.accounts (id) ON DELETE SET NULL DEFERRABLE;

ALTER TABLE lab2.operations_aud
    ADD COLUMN destination_id UUID;

-- История счёта выбирается по обеим сторонам операции в хронологическом порядке.
CREATE INDEX ix_operations_account_at ON lab2.operations (account_id, operation_at);
CREATE INDEX ix_operations_destination_at ON lab2.operations (destination_id, operation_at);

-- Пара (владелец, друг) уникальна.
ALTER TABLE lab2.user_friends
    ADD CONSTRAINT pk_user_friends PRIMARY KEY (owner_id, friend_id);

-- Изменения списка друзей не аудируются: таблица не используется.
DROP TABLE lab2.user_friends_aud;
