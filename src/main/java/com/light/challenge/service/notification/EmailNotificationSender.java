package com.light.challenge.service.notification;

import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.entity.ApproverEntity;
import com.light.challenge.domain.enums.NotificationChannel;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public void send(ApproverEntity approver, InvoiceRequest invoice) {
        System.out.println(
                "Sending approval request via Email to "
                        + approver.getEmail()
                        + " ("
                        + approver.getName()
                        + ") for invoice: amount="
                        + invoice.amount()
                        + ", department="
                        + invoice.department());
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }
}
