package com.light.challenge.domain.entity;

import com.light.challenge.domain.enums.Department;
import com.light.challenge.domain.enums.NotificationChannel;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "approval_request")
@Getter
@Setter
public class ApprovalRequestEntity {

  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rule_id", nullable = false)
  private RuleEntity rule;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approver_id", nullable = false)
  private ApproverEntity approver;

  @Column(name = "invoice_amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal invoiceAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "invoice_department", nullable = false, length = 50)
  private Department invoiceDepartment;

  @Column(name = "requires_manager_approval", nullable = false)
  private Boolean requiresManagerApproval;

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_channel", nullable = false, length = 20)
  private NotificationChannel notificationChannel;

  @Column(name = "sent_at", nullable = false)
  private LocalDateTime sentAt;
}
