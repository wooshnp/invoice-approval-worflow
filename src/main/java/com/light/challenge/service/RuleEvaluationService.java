package com.light.challenge.service;

import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.entity.RuleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RuleEvaluationService {

  public Optional<RuleEntity> findMatchingRule(List<RuleEntity> rules, InvoiceRequest invoice) {
    return rules.stream().filter(rule -> matches(rule, invoice)).findFirst();
  }

  private boolean matches(RuleEntity rule, InvoiceRequest invoice) {
    // Check min amount (if specified)
    if (rule.getMinAmount() != null && invoice.amount().compareTo(rule.getMinAmount()) < 0) {
      return false;
    }

    // Check max amount (if specified)
    if (rule.getMaxAmount() != null && invoice.amount().compareTo(rule.getMaxAmount()) > 0) {
      return false;
    }

    // Check department (if specified)
    if (rule.getDepartment() != null && rule.getDepartment() != invoice.department()) {
      return false;
    }

    // Check manager approval requirement (if specified)
    if (rule.getRequiresManagerApproval() != null
        && rule.getRequiresManagerApproval() != invoice.requiresManagerApproval()) {
      return false;
    }

    return true;
  }
}
