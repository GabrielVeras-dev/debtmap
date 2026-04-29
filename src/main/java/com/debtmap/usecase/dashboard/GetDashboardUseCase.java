package com.debtmap.usecase.dashboard;

import com.debtmap.domain.entity.Debt;
import com.debtmap.domain.entity.Installment;
import com.debtmap.domain.enums.DebtStatus;
import com.debtmap.domain.enums.TransactionType;
import com.debtmap.domain.repository.DebtRepository;
import com.debtmap.domain.repository.InstallmentRepository;
import com.debtmap.domain.repository.TransactionRepository;
import com.debtmap.infrastructure.config.InterestCalculatorService;
import com.debtmap.shared.dto.response.DashboardResponse;
import com.debtmap.shared.dto.response.UpcomingInstallmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetDashboardUseCase {

    private final TransactionRepository transactionRepository;
    private final DebtRepository debtRepository;
    private final InstallmentRepository installmentRepository;
    private final InterestCalculatorService calculatorService;

    @Transactional(readOnly = true)
    public DashboardResponse execute(UUID userId) {

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        // ── Transações do mês atual ───────────────────────────
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.INCOME, firstDayOfMonth, lastDayOfMonth
        );

        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, TransactionType.EXPENSE, firstDayOfMonth, lastDayOfMonth
        );

        BigDecimal balance = totalIncome.subtract(totalExpense);

        // ── Dívidas ───────────────────────────────────────────
        List<Debt> debts = debtRepository.findByUserId(userId);

        BigDecimal totalDebtAmount = BigDecimal.ZERO;
        BigDecimal totalDebtPaid = BigDecimal.ZERO;

        for (Debt debt : debts) {
            BigDecimal installmentAmount = calculatorService.calculateInstallmentAmount(
                    debt.getPrincipalAmount(),
                    debt.getInterestRate(),
                    debt.getTotalInstallments()
            );

            BigDecimal debtTotal = calculatorService.calculateTotalAmount(
                    installmentAmount, debt.getTotalInstallments()
            );

            BigDecimal debtPaid = debt.getInstallments().stream()
                    .filter(i -> Boolean.TRUE.equals(i.getPaid()))
                    .map(i -> i.getPaidAmount() != null ? i.getPaidAmount() : i.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalDebtAmount = totalDebtAmount.add(debtTotal);
            totalDebtPaid = totalDebtPaid.add(debtPaid);
        }

        BigDecimal totalDebtRemaining = totalDebtAmount.subtract(totalDebtPaid)
                .setScale(2, RoundingMode.HALF_UP);

        // contagem de dívidas em aberto
        long openDebtsCount = debts.stream()
                .filter(d -> d.getStatus() == DebtStatus.OPEN || d.getStatus() == DebtStatus.PARTIAL)
                .count();

        // parcelas vencidas e não pagas
        long overdueCount = installmentRepository
                .findByPaidFalseAndDueDateBefore(today)
                .stream()
                .filter(i -> i.getDebt().getUser().getId().equals(userId))
                .count();

        // próximas 5 parcelas a vencer
        List<UpcomingInstallmentResponse> upcoming = debts.stream()
                .flatMap(debt -> debt.getInstallments().stream()
                        .filter(i -> !Boolean.TRUE.equals(i.getPaid()))
                        .filter(i -> !i.getDueDate().isBefore(today))
                        .map(i -> toUpcomingResponse(i, debt))
                )
                .sorted(Comparator.comparing(UpcomingInstallmentResponse::dueDate))
                .limit(5)
                .toList();

        log.debug("Dashboard gerado para usuário {}", userId);

        return new DashboardResponse(
                totalIncome,
                totalExpense,
                balance,
                totalDebtAmount.setScale(2, RoundingMode.HALF_UP),
                totalDebtPaid.setScale(2, RoundingMode.HALF_UP),
                totalDebtRemaining,
                openDebtsCount,
                overdueCount,
                upcoming
        );
    }

    private UpcomingInstallmentResponse toUpcomingResponse(Installment installment, Debt debt) {
        return new UpcomingInstallmentResponse(
                installment.getId(),
                debt.getId(),
                debt.getCreditorName(),
                installment.getInstallmentNumber(),
                debt.getTotalInstallments(),
                installment.getDueDate(),
                installment.getAmount()
        );
    }
}