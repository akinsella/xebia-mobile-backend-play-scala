# --- !Ups

ALTER TABLE news ADD publicationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE news ADD draft boolean DEFAULT true NOT NULL;
ALTER TABLE news ADD targetUrl varchar(255) DEFAULT 'http://' NOT NULL;

# --- !Downs

ALTER TABLE news DROP targetUrl;
ALTER TABLE news DROP status;
ALTER TABLE news DROP publishedAt;
