--liquibase formatted sql
--changeset nunopinho:003-create-rule-table

CREATE TABLE rule
(
    id                        UUID PRIMARY KEY,
    workflow_id                UUID NOT NULL,
    approver_id               UUID NOT NULL,
    priority                  INTEGER NOT NULL,
    min_amount                DECIMAL(15, 2),
    max_amount                DECIMAL(15, 2),
    department                VARCHAR(50),
    requires_manager_approval BOOLEAN,
    notification_channel      VARCHAR(20) NOT NULL,
    created_at                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rule_workflow FOREIGN KEY (workflow_id) REFERENCES workflow (id),
    CONSTRAINT fk_rule_approver FOREIGN KEY (approver_id) REFERENCES approver (id)
);

CREATE INDEX idx_rule_workflow_priority ON rule (workflow_id, priority);

-- rollback DROP INDEX idx_rule_workflow_priority;
-- rollback DROP TABLE rule;
