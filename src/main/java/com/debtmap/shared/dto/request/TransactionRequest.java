package com.debtmap.shared.dto.request;

import com.debtmap.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequest(

        @NotNull(message = "Tipo é obrigatório")
        TransactionType type,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal amount,

        @NotBlank(message = "Descrição é obrigatória")
        String description,

        @NotNull(message = "Data é obrigatória")
        LocalDate transactionDate,

        // categoria é opcional
        UUID categoryId,

        String notes
) {}