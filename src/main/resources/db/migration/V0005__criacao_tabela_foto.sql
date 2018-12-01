CREATE TABLE foto (
  id     UUID NOT NULL,
  image  OID NOT NULL,
  pet_id UUID NOT NULL REFERENCES pet(id),
  CONSTRAINT foto_pk PRIMARY KEY (id)
);