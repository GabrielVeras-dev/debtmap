package com.debtmap.shared.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpcomingInstallmentResponse(
        UUID installmentId,
        UUID debtId,
        String creditorName,
        Integer installmentNumber,
        Integer totalInstallments,
        LocalDate dueDate,
        BigDecimal amount
) {}