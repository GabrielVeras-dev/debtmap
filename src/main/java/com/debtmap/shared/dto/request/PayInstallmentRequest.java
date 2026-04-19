package com.debtmap.shared.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PayInstallmentRequest(

        @NotNull(message = "Valor pago é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor pago deve ser maior que zero")
        BigDecimal paidAmount
) {}