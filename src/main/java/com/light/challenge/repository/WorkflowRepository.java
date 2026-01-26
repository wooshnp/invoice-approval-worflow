package com.light.challenge.repository;

import com.light.challenge.domain.entity.WorkflowEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends CrudRepository<WorkflowEntity, UUID> {

  @Query("SELECT w FROM WorkflowEntity w WHERE w.active = true")
  Optional<WorkflowEntity> findActiveWorkflow();
}
