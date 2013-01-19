# --- !Ups

ALTER TABLE device ADD updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;

# --- !Downs

ALTER TABLE device DROP updatedAt;
