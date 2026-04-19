package com.debtmap.usecase.debt;

import com.debtmap.domain.entity.Debt;
import com.debtmap.domain.entity.Installment;
import com.debtmap.domain.enums.DebtStatus;
import com.debtmap.domain.repository.DebtRepository;
import com.debtmap.domain.repository.InstallmentRepository;
import com.debtmap.infrastructure.mapper.DebtMapper;
import com.debtmap.shared.dto.request.PayInstallmentRequest;
import com.debtmap.shared.dto.response.InstallmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayInstallmentUseCase {

    private final InstallmentRepository installmentRepository;
    private final DebtRepository debtRepository;
    private final DebtMapper debtMapper;

    @Transactional
    public InstallmentResponse execute(UUID debtId, UUID installmentId, PayInstallmentRequest request, UUID userId) {

        // garante que a dívida pertence ao usuário
        Debt debt = debtRepository.findByIdAndUserId(debtId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Dívida não encontrada"));

        Installment installment = installmentRepository.findById(installmentId)
                .filter(i -> i.getDebt().getId().equals(debtId))
                .orElseThrow(() -> new IllegalArgumentException("Parcela não encontrada"));

        if (Boolean.TRUE.equals(installment.getPaid())) {
            throw new IllegalArgumentException("Parcela já foi paga");
        }

        // registra o pagamento
        installment.setPaid(true);
        installment.setPaidAt(LocalDateTime.now());
        installment.setPaidAmount(request.paidAmount());
        installmentRepository.save(installment);

        // atualiza o status da dívida
        updateDebtStatus(debt);

        log.info("Parcela {} da dívida {} paga: {}", installment.getInstallmentNumber(), debtId, request.paidAmount());

        return debtMapper.toInstallmentResponse(installment);
    }

    private void updateDebtStatus(Debt debt) {
        long totalInstallments = debt.getInstallments().size();
        long paidInstallments = debt.getInstallments().stream()
                .filter(i -> Boolean.TRUE.equals(i.getPaid()))
                .count();

        if (paidInstallments == totalInstallments) {
            debt.setStatus(DebtStatus.PAID);
        } else if (paidInstallments > 0) {
            debt.setStatus(DebtStatus.PARTIAL);
        }

        debtRepository.save(debt);
    }
}