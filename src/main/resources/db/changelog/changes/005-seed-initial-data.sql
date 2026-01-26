--liquibase formatted sql
--changeset nunopinho:005-seed-initial-data
--pre-load workflow from Fig1 as required by the challenge

-- Create the default workflow
INSERT INTO workflow (id, name, version, active)
VALUES ('a0000000-0000-0000-0000-000000000001', 'Default Invoice Approval Workflow', 1, TRUE);

-- Create approvers
INSERT INTO approver (id, type, name, email, slack_handle, active)
VALUES ('b0000000-0000-0000-0000-000000000001', 'FINANCE_TEAM_MEMBER', 'Finance Team', 'finance-team@light.inc', '@finance-team', TRUE),
       ('b0000000-0000-0000-0000-000000000002', 'FINANCE_MANAGER', 'Finance Manager', 'finance-manager@light.inc', '@finance-manager', TRUE),
       ('b0000000-0000-0000-0000-000000000003', 'CFO', 'Chief Financial Officer', 'cfo@light.inc', '@cfo', TRUE),
       ('b0000000-0000-0000-0000-000000000004', 'CMO', 'Chief Marketing Officer', 'cmo@light.inc', '@cmo', TRUE);

-- Create rules based on Fig1 workflow
-- Rule 1: Amount > 10000 AND Marketing -> CMO via Email
INSERT INTO rule (id, workflow_id, approver_id, priority, min_amount, max_amount, department, requires_manager_approval, notification_channel)
VALUES ('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000004', 1, 10000.01, NULL, 'MARKETING', NULL, 'EMAIL');

-- Rule 2: Amount > 10000 AND NOT Marketing -> CFO via Slack
INSERT INTO rule (id, workflow_id, approver_id, priority, min_amount, max_amount, department, requires_manager_approval, notification_channel)
VALUES ('c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000003', 2, 10000.01, NULL, NULL, NULL, 'SLACK');

-- Rule 3: 5000 < Amount <= 10000 AND Manager Approval Required -> Finance Manager via Email
INSERT INTO rule (id, workflow_id, approver_id, priority, min_amount, max_amount, department, requires_manager_approval, notification_channel)
VALUES ('c0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 3, 5000.01, 10000.00, NULL, TRUE, 'EMAIL');

-- Rule 4: 5000 < Amount <= 10000 AND No Manager Approval -> Finance Team via Slack
INSERT INTO rule (id, workflow_id, approver_id, priority, min_amount, max_amount, department, requires_manager_approval, notification_channel)
VALUES ('c0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 4, 5000.01, 10000.00, NULL, FALSE, 'SLACK');

-- Rule 5: Amount <= 5000 -> Finance Team via Slack (catch-all for low amounts)
INSERT INTO rule (id, workflow_id, approver_id, priority, min_amount, max_amount, department, requires_manager_approval, notification_channel)
VALUES ('c0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 5, NULL, 5000.00, NULL, NULL, 'SLACK');

-- rollback DELETE FROM rule WHERE workflow_id = 'a0000000-0000-0000-0000-000000000001';
-- rollback DELETE FROM approver WHERE id IN ('b0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000004');
-- rollback DELETE FROM workflow WHERE id = 'a0000000-0000-0000-0000-000000000001';
