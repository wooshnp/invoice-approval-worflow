package com.light.challenge.service.notification;

import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.entity.ApproverEntity;
import com.light.challenge.domain.enums.NotificationChannel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private final Map<NotificationChannel, NotificationSender> senders;

  public NotificationService(List<NotificationSender> senderList) {
    this.senders =
        senderList.stream()
            .collect(Collectors.toMap(NotificationSender::getChannel, sender -> sender));
  }

  public void send(NotificationChannel channel, ApproverEntity approver, InvoiceRequest invoice) {
    var sender = senders.get(channel);
    if (sender == null) {
      throw new IllegalArgumentException("No sender configured for channel: " + channel);
    }
    sender.send(approver, invoice);
  }
}
