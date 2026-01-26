package com.light.challenge.domain.entity;

import com.light.challenge.domain.enums.ApproverType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "approver")
@Getter
@Setter
public class ApproverEntity {

  @Id
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private ApproverType type;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column(name = "slack_handle", length = 100)
  private String slackHandle;

  @Column(nullable = false)
  private Boolean active;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
