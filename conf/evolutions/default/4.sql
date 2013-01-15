# --- !Ups

CREATE TABLE news (
    id SERIAL PRIMARY KEY,
    title varchar(128) NOT NULL,
    content varchar(4096) NOT NULL,
    imageUrl varchar(256) NOT NULL,
	  created_at timestamp NOT NULL,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

# --- !Downs

DROP TABLE news;
