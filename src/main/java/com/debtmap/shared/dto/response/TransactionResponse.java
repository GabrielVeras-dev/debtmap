package com.debtmap.shared.dto.response;

import com.debtmap.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDate transactionDate,
        String notes,
        UUID categoryId,
        String categoryName,
        LocalDateTime createdAt
) {}