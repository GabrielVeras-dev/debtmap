package com.debtmap.usecase.debt;

import com.debtmap.domain.entity.Debt;
import com.debtmap.domain.repository.DebtRepository;
import com.debtmap.infrastructure.config.InterestCalculatorService;
import com.debtmap.infrastructure.mapper.DebtMapper;
import com.debtmap.shared.dto.response.DebtResponse;
import com.debtmap.shared.dto.response.InstallmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetDebtUseCase {

    private final DebtRepository debtRepository;
    private final InterestCalculatorService calculatorService;
    private final DebtMapper debtMapper;

    @Transactional(readOnly = true)
    public DebtResponse execute(UUID debtId, UUID userId) {

        Debt debt = debtRepository.findByIdAndUserId(debtId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Dívida não encontrada"));

        return buildResponse(debt);
    }

    DebtResponse buildResponse(Debt debt) {

        BigDecimal installmentAmount = calculatorService.calculateInstallmentAmount(
                debt.getPrincipalAmount(),
                debt.getInterestRate(),
                debt.getTotalInstallments()
        );

        BigDecimal totalAmount = calculatorService.calculateTotalAmount(
                installmentAmount, debt.getTotalInstallments()
        );

        // soma o que já foi pago
        BigDecimal totalPaid = debt.getInstallments().stream()
                .filter(i -> Boolean.TRUE.equals(i.getPaid()))
                .map(i -> i.getPaidAmount() != null ? i.getPaidAmount() : i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = totalAmount.subtract(totalPaid);

        List<InstallmentResponse> installmentResponses = debt.getInstallments()
                .stream()
                .map(debtMapper::toInstallmentResponse)
                .toList();

        return new DebtResponse(
                debt.getId(),
                debt.getCreditorName(),
                debt.getDescription(),
                debt.getPrincipalAmount(),
                debt.getInterestRate(),
                debt.getTotalInstallments(),
                debt.getStartDate(),
                debt.getStatus(),
                totalAmount,
                totalPaid,
                remaining,
                installmentResponses,
                debt.getCreatedAt()
        );
    }
}