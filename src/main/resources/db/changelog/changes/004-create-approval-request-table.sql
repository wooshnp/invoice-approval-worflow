--liquibase formatted sql
--changeset nunopinho:004-create-approval-request-table

CREATE TABLE approval_request
(
    id                        UUID PRIMARY KEY,
    rule_id                   UUID           NOT NULL,
    approver_id               UUID           NOT NULL,
    invoice_amount            DECIMAL(15, 2) NOT NULL,
    invoice_department        VARCHAR(50)    NOT NULL,
    requires_manager_approval BOOLEAN        NOT NULL,
    notification_channel      VARCHAR(20)    NOT NULL,
    sent_at                   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_approval_request_rule FOREIGN KEY (rule_id) REFERENCES rule (id),
    CONSTRAINT fk_approval_request_approver FOREIGN KEY (approver_id) REFERENCES approver (id)
);

CREATE INDEX idx_approval_request_sent_at ON approval_request (sent_at DESC);

-- rollback DROP INDEX idx_approval_request_sent_at;
-- rollback DROP TABLE approval_request;
