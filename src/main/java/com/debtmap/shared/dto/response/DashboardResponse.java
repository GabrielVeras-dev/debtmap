package com.debtmap.shared.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(

        // resumo do mês atual
        BigDecimal totalIncomeCurrentMonth,
        BigDecimal totalExpenseCurrentMonth,
        BigDecimal balanceCurrentMonth,

        // resumo geral de dívidas
        BigDecimal totalDebtAmount,
        BigDecimal totalDebtPaid,
        BigDecimal totalDebtRemaining,
        Long openDebtsCount,
        Long overdueInstallmentsCount,

        // próximas parcelas a vencer (5 mais próximas)
        List<UpcomingInstallmentResponse> upcomingInstallments
) {}