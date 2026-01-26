package com.light.challenge.repository;

import com.light.challenge.domain.entity.ApprovalRequestEntity;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRequestRepository extends CrudRepository<ApprovalRequestEntity, UUID> {
}
