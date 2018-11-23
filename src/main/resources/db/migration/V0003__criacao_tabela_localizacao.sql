CREATE TABLE localizacao (
  id        UUID NOT NULL,
  latitude  NUMERIC(15) NOT NULL,
  longitude NUMERIC(15) NOT NULL,
  cidade    CHARACTER VARYING(40) NOT NULL,
  estado    CHARACTER VARYING(20) NOT NULL,
  CONSTRAINT localizacao_pk PRIMARY KEY (id)
);