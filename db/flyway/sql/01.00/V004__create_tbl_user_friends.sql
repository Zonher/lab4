create table lab2.user_friends
(
    owner_id  UUID NOT NULL,
    friend_id UUID NOT NULL,
    CONSTRAINT fk_frnds_owner_id FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE DEFERRABLE,
    CONSTRAINT fk_frnds_frnd_id FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE DEFERRABLE
);

create table lab2.user_friends_aud
(
    owner_id  UUID                     NOT NULL,
    friend_id UUID                     ,
    rev       timestamp with time zone NOT NULL default now(),
    revtype   integer
)