package com.light.challenge.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.light.challenge.domain.dto.InvoiceRequest;
import com.light.challenge.domain.entity.RuleEntity;
import com.light.challenge.domain.enums.Department;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RuleEvaluationServiceTest {

  private final RuleEvaluationService service = new RuleEvaluationService();

  @Test
  @DisplayName("returns first matching rule in order")
  void findMatchingRule_returnsFirstMatchingRuleInOrder() {
    var first = new RuleEntity();
    first.setMinAmount(new BigDecimal("0.00"));
    first.setMaxAmount(new BigDecimal("5000.00"));
    first.setDepartment(Department.OTHER);
    first.setRequiresManagerApproval(false);

    var second = new RuleEntity();
    second.setMinAmount(new BigDecimal("0.00"));
    second.setMaxAmount(new BigDecimal("10000.00"));
    second.setDepartment(Department.OTHER);
    second.setRequiresManagerApproval(false);

    var invoice = new InvoiceRequest(new BigDecimal("3000.00"), Department.OTHER, false);

    var result = service.findMatchingRule(List.of(first, second), invoice);

    assertThat(result).contains(first);
  }

  @Test
  @DisplayName("respects amount, department, and manager approval constraints")
  void findMatchingRule_respectsAmountDepartmentAndManagerApprovalConstraints() {
    var rule = new RuleEntity();
    rule.setMinAmount(new BigDecimal("5000.01"));
    rule.setMaxAmount(new BigDecimal("10000.00"));
    rule.setDepartment(Department.MARKETING);
    rule.setRequiresManagerApproval(true);

    var tooLow = new InvoiceRequest(new BigDecimal("5000.00"), Department.MARKETING, true);
    var wrongDept = new InvoiceRequest(new BigDecimal("6000.00"), Department.OTHER, true);
    var wrongApproval = new InvoiceRequest(new BigDecimal("6000.00"), Department.MARKETING, false);
    var matches = new InvoiceRequest(new BigDecimal("6000.00"), Department.MARKETING, true);

    assertThat(service.findMatchingRule(List.of(rule), tooLow)).isEmpty();
    assertThat(service.findMatchingRule(List.of(rule), wrongDept)).isEmpty();
    assertThat(service.findMatchingRule(List.of(rule), wrongApproval)).isEmpty();
    assertThat(service.findMatchingRule(List.of(rule), matches)).contains(rule);
  }
}
