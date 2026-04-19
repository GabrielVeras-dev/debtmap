package com.debtmap.shared.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record InstallmentResponse(
        UUID id,
        Integer installmentNumber,
        LocalDate dueDate,
        BigDecimal amount,
        Boolean paid,
        LocalDateTime paidAt,
        BigDecimal paidAmount
) {}