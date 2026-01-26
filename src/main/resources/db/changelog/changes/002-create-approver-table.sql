--liquibase formatted sql
--changeset nunopinho:002-create-approver-table

CREATE TABLE approver
(
    id           UUID PRIMARY KEY,
    type         VARCHAR(50)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    slack_handle VARCHAR(100),
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_approver_type ON approver (type);

-- rollback DROP INDEX idx_approver_type;
-- rollback DROP TABLE approver;
