create table lab2.auth_accounts
(
    id            UUID PRIMARY KEY DEFAULT uuidv7(),
    login         varchar(20)  NOT NULL UNIQUE,
    password_hash varchar(100) NOT NULL,
    role          varchar(10)  NOT NULL
        check (role in ('ADMIN', 'CLIENT')),

    user_id       UUID UNIQUE,

    constraint fk_auth_account_user
        foreign key (user_id)
            references lab2.users(id)
            on delete cascade,

    constraint chk_auth_account_role
        check (
            (role = 'ADMIN' and user_id is null)
                or
            (role = 'CLIENT' and user_id is not null)
            )
);