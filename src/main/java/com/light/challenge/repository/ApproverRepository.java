package com.light.challenge.repository;

import com.light.challenge.domain.entity.ApproverEntity;
import com.light.challenge.domain.enums.ApproverType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApproverRepository extends CrudRepository<ApproverEntity, UUID> {

  @Query("SELECT a FROM ApproverEntity a WHERE a.type = :type AND a.active = true")
  Optional<ApproverEntity> findActiveApproverByType(ApproverType type);
}
