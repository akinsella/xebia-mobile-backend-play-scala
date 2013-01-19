# --- !Ups

ALTER TABLE news ADD publishedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE news ADD status varchar(64) DEFAULT 'DRAFT' NOT NULL;

# --- !Downs

ALTER TABLE news DROP status;
ALTER TABLE news DROP publishedAt;
