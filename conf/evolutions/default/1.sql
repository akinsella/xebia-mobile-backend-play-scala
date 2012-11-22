# --- !Ups

CREATE TABLE Device (
    id SERIAL PRIMARY KEY,
    uuid varchar(255) NOT NULL,
    createdAt timestamp NOT NULL
);

# --- !Downs

DROP TABLE Device;
