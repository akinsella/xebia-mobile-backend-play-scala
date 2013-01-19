# --- !Ups

CREATE TABLE device (
    id SERIAL PRIMARY KEY,
    udid varchar(40) NOT NULL,
	  token varchar(64) NOT NULL,
	  createdAt timestamp NOT NULL
);

# --- !Downs

DROP TABLE device;
