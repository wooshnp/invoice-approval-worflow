package com.light.challenge.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workflow")
@Getter
@Setter
public class WorkflowEntity {

  @Id private UUID id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private Integer version;

  @Column(nullable = false)
  private Boolean active;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY)
  private List<RuleEntity> rules = new ArrayList<>();
}
