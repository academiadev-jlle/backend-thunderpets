CREATE TABLE foto (
  id     UUID NOT NULL,
  image  BYTEA NOT NULL,
  pet_id UUID NOT NULL REFERENCES pet(id),
  CONSTRAINT foto_pk PRIMARY KEY (id)
);