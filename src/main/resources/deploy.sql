CREATE TABLE IF NOT EXISTS sw_users (
    id                  varchar(36)   NOT NULL,
    username            varchar(20)   NOT NULL,
    active_kit          int default 0 NOT NULL,
    active_trail        int default 0 NOT NULL,
    reputation          int default 10 NOT NULL,
    last_vote_timestamp bigint default 0 NOT NULL,
    games               int default 0 NOT NULL,
    wins                int default 0 NOT NULL,
    kills               int default 0 NOT NULL,
    deaths              int default 0 NOT NULL,
    arrows_fired        int default 0 NOT NULL,
    blocks_placed       int default 0 NOT NULL,
    blocks_broken       int default 0 NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (id));

CREATE TABLE IF NOT EXISTS sw_kits (
    id      varchar(36) NOT NULL,
    kit_id  int  NOT NULL DEFAULT 0,
    PRIMARY KEY (id, kit_id)
);

CREATE TABLE IF NOT EXISTS sw_trails (
    id                varchar(36) NOT NULL,
    trail_id          int NOT NULL default 0,
    trail_name        varchar(20) NOT NULL,
    trail_description varchar(255) NOT NULL,
    trail_particle    varchar(20) NOT NULL,
    PRIMARY KEY (id, trail_id, trail_name, trail_description, trail_particle)
);