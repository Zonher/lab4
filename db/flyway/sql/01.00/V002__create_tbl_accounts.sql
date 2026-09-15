create table lab2.accounts
(
    id      UUID PRIMARY KEY        DEFAULT uuidv7(),
    user_id UUID           NOT NULL,
    balance NUMERIC(10, 2) NOT NULL DEFAULT 0.0,
    CONSTRAINT fk_acc_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE DEFERRABLE
);

create table lab2.accounts_aud
(
    id      UUID                     NOT NULL,
    user_id   UUID              NOT NULL,
    balance NUMERIC(10, 2)           NOT NULL,
    rev     timestamp with time zone NOT NULL default now(),
    revtype integer
);