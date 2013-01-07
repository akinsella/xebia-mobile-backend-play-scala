# --- !Ups

CREATE TABLE news (
    id SERIAL PRIMARY KEY,
    udid varchar(40) NOT NULL,
	  token varchar(64) NOT NULL,
	  created_at timestamp NOT NULL,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

# --- !Downs

DROP TABLE news;
