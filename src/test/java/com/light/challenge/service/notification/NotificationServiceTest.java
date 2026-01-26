package com.light.challenge.service.notification;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.entity.ApproverEntity;
import com.light.challenge.domain.enums.Department;
import com.light.challenge.domain.enums.NotificationChannel;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationServiceTest {

  @Test
  @DisplayName("dispatches to matching sender")
  void send_dispatchesToMatchingSender() {
    var emailSender = mock(NotificationSender.class);
    var slackSender = mock(NotificationSender.class);
    when(emailSender.getChannel()).thenReturn(NotificationChannel.EMAIL);
    when(slackSender.getChannel()).thenReturn(NotificationChannel.SLACK);

    var service = new NotificationService(List.of(emailSender, slackSender));
    clearInvocations(
        emailSender,
        slackSender); // Clear invocations from setup (the ones coming from the constructor)
    var approver = new ApproverEntity();
    var invoice = new InvoiceRequest(new BigDecimal("15000.00"), Department.MARKETING, false);

    service.send(NotificationChannel.EMAIL, approver, invoice);

    verify(emailSender).send(approver, invoice);
    verifyNoInteractions(slackSender);
  }

  @Test
  @DisplayName("throws when no sender is configured")
  void send_throwsWhenNoSenderIsConfigured() {
    var slackSender = mock(NotificationSender.class);
    when(slackSender.getChannel()).thenReturn(NotificationChannel.SLACK);
    var service = new NotificationService(List.of(slackSender));
    var approver = new ApproverEntity();
    var invoice = new InvoiceRequest(new BigDecimal("15000.00"), Department.MARKETING, false);

    assertThatThrownBy(() -> service.send(NotificationChannel.EMAIL, approver, invoice))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No sender configured for channel");
  }
}
