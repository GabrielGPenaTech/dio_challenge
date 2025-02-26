--liquibase formatted sql
--changeset gabriel:202525021959
--comment: blocks table create

CREATE TABLE blocks (
    "id" BIGSERIAL PRIMARY KEY ,
    block_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "block_reason" varchar(255) NOT NULL,
    unblock_at TIMESTAMP NULL,
    "unblock_reason" varchar(255) NULL,
    card_id BIGINT NOT NULL ,
    CONSTRAINT card__block_fk FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);

--rollback DROP TABLE blocks