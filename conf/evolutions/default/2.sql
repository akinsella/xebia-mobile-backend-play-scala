# --- !Ups

ALTER TABLE device ADD last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;

# --- !Downs

ALTER TABLE device DROP last_modified;
