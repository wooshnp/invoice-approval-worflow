package com.light.challenge.domain.dto;

import com.light.challenge.domain.enums.Department;
import java.math.BigDecimal;

public record InvoiceRequest(
    BigDecimal amount, Department department, boolean requiresManagerApproval) {}
