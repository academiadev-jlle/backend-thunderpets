CREATE TABLE pet (
  id              UUID NOT NULL,
  nome            CHARACTER VARYING NOT NULL,
  descricao       CHARACTER VARYING NOT NULL,
  data_achado     DATE NOT NULL,
  data_registro   DATE NOT NULL,
  especie         CHARACTER VARYING NOT NULL,
  porte           CHARACTER VARYING NOT NULL,
  sexo            CHARACTER VARYING NOT NULL,
  status          CHARACTER VARYING NOT NULL,
  idade           CHARACTER VARYING NOT NULL,
  ativo           BOOLEAN DEFAULT TRUE,
  localizacao_id  UUID REFERENCES localizacao(id),
  usuario_id      UUID NOT NULL REFERENCES usuario(id),
  CONSTRAINT pet_pk PRIMARY KEY (id)
);