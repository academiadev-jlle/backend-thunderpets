CREATE TABLE pet (
  id           UUID NOT NULL,
  nome         CHARACTER VARYING NOT NULL,
  descricao    CHARACTER VARYING NOT NULL,
  dataAchado   DATE NOT NULL,
  dataRegistro DATE NOT NULL,
  especie      CHARACTER VARYING NOT NULL,
  porte        CHARACTER VARYING NOT NULL,
  sexo         CHARACTER VARYING NOT NULL,
  status       CHARACTER VARYING NOT NULL,
  idade        CHARACTER VARYING NOT NULL,
  ativo        BOOLEAN DEFAULT TRUE,
  localizacao  CHARACTER VARYING NOT NULL REFERENCES localizacao(id),
  usuario_id   CHARACTER VARYING NOT NULL REFERENCES usuario(id),
  CONSTRAINT pet_pk PRIMARY KEY (id)
);