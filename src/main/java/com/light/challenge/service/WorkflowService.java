package com.light.challenge.service;

import com.light.challenge.domain.dto.ApprovalResult;
import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.entity.ApprovalRequestEntity;
import com.light.challenge.repository.ApprovalRequestRepository;
import com.light.challenge.repository.RuleRepository;
import com.light.challenge.repository.WorkflowRepository;
import com.light.challenge.service.notification.NotificationService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowService {

  private final WorkflowRepository workflowRepository;
  private final RuleRepository ruleRepository;
  private final RuleEvaluationService ruleEvaluationService;
  private final NotificationService notificationService;
  private final ApprovalRequestRepository approvalRequestRepository;

  public WorkflowService(
      WorkflowRepository workflowRepository,
      RuleRepository ruleRepository,
      RuleEvaluationService ruleEvaluationService,
      NotificationService notificationService,
      ApprovalRequestRepository approvalRequestRepository) {
    this.workflowRepository = workflowRepository;
    this.ruleRepository = ruleRepository;
    this.ruleEvaluationService = ruleEvaluationService;
    this.notificationService = notificationService;
    this.approvalRequestRepository = approvalRequestRepository;
  }

  @Transactional
  public ApprovalResult processInvoice(InvoiceRequest invoice) {
    // Get active workflow
    var workflow =
        workflowRepository
            .findActiveWorkflow()
            .orElseThrow(() -> new IllegalStateException("No active workflow found"));

    // Get rules ordered by priority
    var rules = ruleRepository.findRulesByWorkflowId(workflow.getId());

    // Find matching rule
    var matchingRule = ruleEvaluationService.findMatchingRule(rules, invoice);

    if (matchingRule.isEmpty()) {
      return new ApprovalResult(false, null, null, null, "No matching rule found for invoice");
    }

    var rule = matchingRule.get();
    var approver = rule.getApprover();

    // Send notification
    notificationService.send(rule.getNotificationChannel(), approver, invoice);

    // Save audit log
    var auditRecord = new ApprovalRequestEntity();
    auditRecord.setId(UUID.randomUUID());
    auditRecord.setRule(rule);
    auditRecord.setApprover(approver);
    auditRecord.setInvoiceAmount(invoice.amount());
    auditRecord.setInvoiceDepartment(invoice.department());
    auditRecord.setRequiresManagerApproval(invoice.requiresManagerApproval());
    auditRecord.setNotificationChannel(rule.getNotificationChannel());
    auditRecord.setSentAt(LocalDateTime.now());
    approvalRequestRepository.save(auditRecord);

    return new ApprovalResult(
        true,
        approver.getName(),
        approver.getType(),
        rule.getNotificationChannel(),
        "Approval request sent successfully");
  }
}
