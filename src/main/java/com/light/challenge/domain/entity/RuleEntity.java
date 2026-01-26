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
@Table(name = "rule")
@Getter
@Setter
public class RuleEntity {

  @Id
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workflow_id", nullable = false)
  private WorkflowEntity workflow;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "approver_id", nullable = false)
  private ApproverEntity approver;

  @Column(nullable = false)
  private Integer priority;

  @Column(name = "min_amount", precision = 15, scale = 2)
  private BigDecimal minAmount;

  @Column(name = "max_amount", precision = 15, scale = 2)
  private BigDecimal maxAmount;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private Department department;

  @Column(name = "requires_manager_approval")
  private Boolean requiresManagerApproval;

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_channel", nullable = false, length = 20)
  private NotificationChannel notificationChannel;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
