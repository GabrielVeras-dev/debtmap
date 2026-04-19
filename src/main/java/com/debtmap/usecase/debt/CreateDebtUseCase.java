package com.debtmap.usecase.debt;

import com.debtmap.domain.entity.Debt;
import com.debtmap.domain.entity.Installment;
import com.debtmap.domain.entity.User;
import com.debtmap.domain.enums.DebtStatus;
import com.debtmap.domain.repository.DebtRepository;
import com.debtmap.domain.repository.UserRepository;
import com.debtmap.infrastructure.config.InterestCalculatorService;
import com.debtmap.infrastructure.mapper.DebtMapper;
import com.debtmap.shared.dto.request.DebtRequest;
import com.debtmap.shared.dto.response.DebtResponse;
import com.debtmap.shared.dto.response.InstallmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateDebtUseCase {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final InterestCalculatorService calculatorService;
    private final DebtMapper debtMapper;

    @Transactional
    public DebtResponse execute(DebtRequest request, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // calcula o valor de cada parcela com juros compostos
        BigDecimal installmentAmount = calculatorService.calculateInstallmentAmount(
                request.principalAmount(),
                request.interestRate(),
                request.totalInstallments()
        );

        Debt debt = Debt.builder()
                .user(user)
                .creditorName(request.creditorName())
                .description(request.description())
                .principalAmount(request.principalAmount())
                .interestRate(request.interestRate())
                .totalInstallments(request.totalInstallments())
                .startDate(request.startDate())
                .status(DebtStatus.OPEN)
                .installments(new ArrayList<>())
                .build();

        // gera as parcelas automaticamente
        for (int i = 1; i <= request.totalInstallments(); i++) {
            Installment installment = Installment.builder()
                    .debt(debt)
                    .installmentNumber(i)
                    .dueDate(request.startDate().plusMonths(i))
                    .amount(installmentAmount)
                    .paid(false)
                    .build();
            debt.getInstallments().add(installment);
        }

        debtRepository.save(debt);
        log.info("Dívida criada: {} parcelas de {} para usuário {}",
                request.totalInstallments(), installmentAmount, userId);

        return buildResponse(debt, installmentAmount);
    }

    private DebtResponse buildResponse(Debt debt, BigDecimal installmentAmount) {
        BigDecimal totalAmount = calculatorService.calculateTotalAmount(
                installmentAmount, debt.getTotalInstallments()
        );

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
                BigDecimal.ZERO,
                totalAmount,
                installmentResponses,
                debt.getCreatedAt()
        );
    }
}