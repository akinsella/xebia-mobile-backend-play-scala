# --- !Ups

CREATE TABLE device (
    id SERIAL PRIMARY KEY,
    udid varchar(40) NOT NULL,
	  token varchar(64) NOT NULL,
	  created_at timestamp NOT NULL
);

# --- !Downs

DROP TABLE device;
