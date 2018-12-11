CREATE TABLE login_social (
  id UUID NOT NULL,
  id_social VARCHAR(500) NOT NULL,
  usuario_id UUID NOT NULL REFERENCES usuario(id),
  CONSTRAINT login_social_pk PRIMARY KEY (id)
);

-- ALTER TABLE usuario ALTER COLUMN senha DROP NOT NULL;