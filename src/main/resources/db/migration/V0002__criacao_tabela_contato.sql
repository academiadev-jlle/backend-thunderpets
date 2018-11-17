CREATE TABLE contato (
  id         UUID NOT NULL,
  descricao  CHARACTER VARYING(99) NOT NULL,
  tipo       CHARACTER VARYING(12) NOT NULL,
  usuario_id CHARACTER VARYING REFERENCES usuario(id),
  CONSTRAINT contato_pk PRIMARY KEY (id)
);