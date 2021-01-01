CREATE TABLE IF NOT EXISTS sw_users (
    id           varchar(36)   NOT NULL,
    username     varchar(20)   NOT NULL,
    active_kit   int default 0 NOT NULL,
    reputation   int default 10 NOT NULL,
    last_vote_timestamp bigint default 0 NOT NULL,
    games        int default 0 NOT NULL,
    wins         int default 0 NOT NULL,
    kills        int default 0 NOT NULL,
    deaths       int default 0 NOT NULL,
    arrows_fired  int default 0 NOT NULL,
    blocks_placed int default 0 NOT NULL,
    blocks_broken int default 0 NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (id));

CREATE TABLE IF NOT EXISTS sw_kits (
    id     varchar(36) NOT NULL,
    kit_id int         NOT NULL DEFAULT 0,
    PRIMARY KEY (id, kit_id)
);