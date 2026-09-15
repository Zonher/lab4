create table lab2.users
(
    id         UUID PRIMARY KEY DEFAULT uuidv7(),
    login      varchar(20) NOT NULL UNIQUE,
    name       varchar(50) NOT NULL,
    age        integer     NOT NULL,
    gender     varchar check ( gender in ('MALE', 'FEMALE') ),
    hair_color varchar check ( hair_color in ('BLACK', 'BLONDE', 'RED', 'COLORED') )
);

create table lab2.users_aud
(
    id         UUID                     NOT NULL,
    login      varchar(20)              NOT NULL,
    name       varchar(50)              NOT NULL,
    age        integer                  NOT NULL,
    gender     varchar,
    hair_color varchar,
    rev        timestamp with time zone NOT NULL default now(),
    revtype    integer
);
