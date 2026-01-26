package com.light.challenge.domain.dto;

import com.light.challenge.domain.enums.ApproverType;
import com.light.challenge.domain.enums.NotificationChannel;

public record ApprovalResult(
    boolean success,
    String approverName,
    ApproverType approverType,
    NotificationChannel channel,
    String message) {}
