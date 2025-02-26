--liquibase formatted sql
--changeset gabriel:202525021959
--comment: boards_column table create

CREATE TABLE boards_column (
    "id" BIGSERIAL PRIMARY KEY ,
    "name" varchar(100)   NOT NULL,
    "type" varchar(50)   NOT NULL,
    "order" int  NOT NULL,
    board_id BIGINT NOT NULL,
    CONSTRAINT boards__board_column_fk FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
    CONSTRAINT id__order_uk UNIQUE (board_id, "order")
);

--rollback DROP TABLE boards_column