package com.light.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.light.challenge.domain.dto.ApprovalResult;
import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.enums.ApproverType;
import com.light.challenge.domain.enums.Department;
import com.light.challenge.domain.enums.NotificationChannel;
import com.light.challenge.service.WorkflowService;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ApprovalWorkflowEntityApplicationTests {

  @Mock private WorkflowService workflowService;

  @Captor private ArgumentCaptor<InvoiceRequest> invoiceCaptor;

  @Test
  @DisplayName("parses CLI arguments and invokes workflow service")
  void run_parsesCliArgumentsAndInvokesWorkflowService() throws Exception {
    var app = new ApprovalWorkflowApplication(workflowService);
    when(workflowService.processInvoice(invoiceCaptor.capture()))
        .thenReturn(
            new ApprovalResult(true, "CFO", ApproverType.CFO, NotificationChannel.SLACK, "ok"));

    app.run("--amount=15000", "--department=MARKETING", "--managerApproval=false");

    verify(workflowService).processInvoice(invoiceCaptor.getValue());
    var invoice = invoiceCaptor.getValue();
    assertThat(invoice.amount()).isEqualByComparingTo(new BigDecimal("15000"));
    assertThat(invoice.department()).isEqualTo(Department.MARKETING);
    assertThat(invoice.requiresManagerApproval()).isFalse();
  }
}
