--liquibase formatted sql
--changeset gabriel:202525021959
--comment: cards table create

CREATE TABLE cards (
    "id" BIGSERIAL PRIMARY KEY,
    "title" varchar(150) NOT NULL,
    "description" varchar(255) NOT NULL,
    board_column_id BIGINT NOT NULL,
    CONSTRAINT boards_columns__cards_fk FOREIGN KEY (board_column_id) REFERENCES boards_column(id) ON DELETE CASCADE
);

--rollback DROP TABLE cards