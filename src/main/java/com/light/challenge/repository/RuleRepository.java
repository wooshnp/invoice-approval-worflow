package com.light.challenge.repository;

import com.light.challenge.domain.entity.RuleEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends CrudRepository<RuleEntity, UUID> {

  @Query("SELECT r FROM RuleEntity r WHERE r.workflow.id = :workflowId ORDER BY r.priority ASC")
  List<RuleEntity> findRulesByWorkflowId(UUID workflowId);
}
