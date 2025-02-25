--liquibase formatted sql
--changeset gabriel:202525021959
--comment: boards table create

CREATE TABLE boards (
    id BIGSERIAL PRIMARY KEY ,
    nome VARCHAR(255) NOT NULL
);

--rollback DROP TABLE BOARDS