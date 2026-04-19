package com.debtmap.shared.dto.response;

import com.debtmap.domain.enums.DebtStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DebtResponse(
        UUID id,
        String creditorName,
        String description,
        BigDecimal principalAmount,
        BigDecimal interestRate,
        Integer totalInstallments,
        LocalDate startDate,
        DebtStatus status,
        BigDecimal totalAmount,        // valor total com juros
        BigDecimal totalPaid,          // valor já pago
        BigDecimal remainingAmount,    // valor restante
        List<InstallmentResponse> installments,
        LocalDateTime createdAt
) {}