package com.light.challenge.service.notification;

import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.entity.ApproverEntity;
import com.light.challenge.domain.enums.NotificationChannel;

public interface NotificationSender {

  void send(ApproverEntity approver, InvoiceRequest invoice);

  NotificationChannel getChannel();
}
