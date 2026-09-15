create table lab2.operations
(
    id             UUID PRIMARY KEY                  DEFAULT uuidv7(),
    account_id     UUID                     NOT NULL,
    operation_type varchar                  NOT NULL check ( operation_type in ('DEPOSIT', 'WITHDRAW', 'TRANSFER') ),
    amount         NUMERIC(10, 2)           NOT NULL,
    operation_at   timestamp with time zone NOT NULL default now(),
    CONSTRAINT fk_op_acc_id FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE DEFERRABLE
);

create table lab2.operations_aud
(
    id             UUID                     NOT NULL,
    account_id     UUID                     NOT NULL,
    operation_type varchar                 NOT NULL,
    amount         NUMERIC(10, 2)           NOT NULL,
    operation_at   timestamp with time zone NOT NULL,
    rev            timestamp with time zone NOT NULL default now(),
    revtype        integer
);