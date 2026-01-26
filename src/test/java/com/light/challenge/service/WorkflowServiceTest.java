package com.light.challenge.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.entity.ApproverEntity;
import com.light.challenge.domain.entity.RuleEntity;
import com.light.challenge.domain.entity.WorkflowEntity;
import com.light.challenge.domain.enums.ApproverType;
import com.light.challenge.domain.enums.Department;
import com.light.challenge.domain.enums.NotificationChannel;
import com.light.challenge.repository.ApprovalRequestRepository;
import com.light.challenge.repository.RuleRepository;
import com.light.challenge.repository.WorkflowRepository;
import com.light.challenge.service.notification.NotificationService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

  @Mock private WorkflowRepository workflowRepository;
  @Mock private RuleRepository ruleRepository;
  @Mock private RuleEvaluationService ruleEvaluationService;
  @Mock private NotificationService notificationService;
  @Mock private ApprovalRequestRepository approvalRequestRepository;

  @InjectMocks private WorkflowService workflowService;

  @Captor
  private ArgumentCaptor<com.light.challenge.domain.entity.ApprovalRequestEntity> auditCaptor;

  @Test
  @DisplayName("sends notification and saves audit when rule matches")
  void processInvoice_sendsNotificationAndSavesAuditWhenRuleMatches() {
    var workflow = new WorkflowEntity();
    var workflowId = UUID.randomUUID();
    workflow.setId(workflowId);

    var approver = new ApproverEntity();
    approver.setName("CFO");
    approver.setType(ApproverType.CFO);

    var rule = new RuleEntity();
    rule.setId(UUID.randomUUID());
    rule.setWorkflow(workflow);
    rule.setApprover(approver);
    rule.setNotificationChannel(NotificationChannel.SLACK);

    var invoice = new InvoiceRequest(new BigDecimal("15000.00"), Department.OTHER, false);

    when(workflowRepository.findActiveWorkflow()).thenReturn(Optional.of(workflow));
    when(ruleRepository.findRulesByWorkflowId(workflowId)).thenReturn(List.of(rule));
    when(ruleEvaluationService.findMatchingRule(List.of(rule), invoice))
        .thenReturn(Optional.of(rule));

    var result = workflowService.processInvoice(invoice);

    assertThat(result.success()).isTrue();
    assertThat(result.approverName()).isEqualTo("CFO");
    assertThat(result.approverType()).isEqualTo(ApproverType.CFO);
    assertThat(result.channel()).isEqualTo(NotificationChannel.SLACK);

    verify(notificationService).send(NotificationChannel.SLACK, approver, invoice);
    verify(approvalRequestRepository).save(auditCaptor.capture());

    var audit = auditCaptor.getValue();
    assertThat(audit.getRule()).isEqualTo(rule);
    assertThat(audit.getApprover()).isEqualTo(approver);
    assertThat(audit.getInvoiceAmount()).isEqualByComparingTo("15000.00");
    assertThat(audit.getInvoiceDepartment()).isEqualTo(Department.OTHER);
    assertThat(audit.getRequiresManagerApproval()).isFalse();
    assertThat(audit.getNotificationChannel()).isEqualTo(NotificationChannel.SLACK);
  }

  @Test
  @DisplayName("returns failure when no matching rule exists")
  void processInvoice_returnsFailureWhenNoMatchingRuleExists() {
    var workflow = new WorkflowEntity();
    var workflowId = UUID.randomUUID();
    workflow.setId(workflowId);

    var invoice = new InvoiceRequest(new BigDecimal("15000.00"), Department.OTHER, false);

    when(workflowRepository.findActiveWorkflow()).thenReturn(Optional.of(workflow));
    when(ruleRepository.findRulesByWorkflowId(workflowId)).thenReturn(List.of());
    when(ruleEvaluationService.findMatchingRule(List.of(), invoice)).thenReturn(Optional.empty());

    var result = workflowService.processInvoice(invoice);

    assertThat(result.success()).isFalse();
    assertThat(result.message()).contains("No matching rule");
    verify(notificationService, never()).send(any(), any(), any());
    verify(approvalRequestRepository, never()).save(any());
  }
}
