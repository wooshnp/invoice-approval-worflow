--liquibase formatted sql
--changeset nunopinho:001-create-workflow-table

CREATE TABLE workflow
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    version    INTEGER      NOT NULL DEFAULT 1,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_workflow_name_version UNIQUE (name, version)
);

-- rollback DROP TABLE workflow;
