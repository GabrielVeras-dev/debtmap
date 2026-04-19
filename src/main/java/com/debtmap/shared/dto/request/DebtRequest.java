package com.debtmap.shared.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtRequest(

        @NotBlank(message = "Nome do credor é obrigatório")
        String creditorName,

        String description,

        @NotNull(message = "Valor principal é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal principalAmount,

        // taxa de juros mensal em decimal (ex: 0.025 = 2.5% ao mês)
        @NotNull(message = "Taxa de juros é obrigatória")
        @DecimalMin(value = "0.0", message = "Taxa de juros não pode ser negativa")
        BigDecimal interestRate,

        @NotNull(message = "Número de parcelas é obrigatório")
        @Min(value = 1, message = "Número de parcelas deve ser pelo menos 1")
        Integer totalInstallments,

        @NotNull(message = "Data de início é obrigatória")
        LocalDate startDate
) {}