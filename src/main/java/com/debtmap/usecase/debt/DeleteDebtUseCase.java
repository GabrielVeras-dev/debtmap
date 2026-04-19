package com.debtmap.usecase.debt;

import com.debtmap.domain.repository.DebtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteDebtUseCase {

    private final DebtRepository debtRepository;

    @Transactional
    public void execute(UUID debtId, UUID userId) {
        debtRepository.findByIdAndUserId(debtId, userId)
                .ifPresentOrElse(
                        debt -> {
                            debtRepository.delete(debt);
                            log.info("Dívida deletada: {}", debtId);
                        },
                        () -> { throw new IllegalArgumentException("Dívida não encontrada"); }
                );
    }
}