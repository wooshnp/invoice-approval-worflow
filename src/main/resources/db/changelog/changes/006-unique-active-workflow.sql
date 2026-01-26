--liquibase formatted sql
--changeset nunopinho:006-unique-active-workflow

CREATE UNIQUE INDEX ux_workflow_single_active
    ON workflow (active)
    WHERE active;

-- rollback DROP INDEX ux_workflow_single_active;
