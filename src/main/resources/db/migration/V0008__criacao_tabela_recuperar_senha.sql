CREATE TABLE recuperar_senha (
  id            UUID NOT NULL,
  usuario_id    UUID NOT NULL REFERENCES usuario(id),
  ativo         BOOLEAN DEFAULT TRUE,
  created_at    TIMESTAMP,
  CONSTRAINT recuperarsenha_pk PRIMARY KEY (id)
);