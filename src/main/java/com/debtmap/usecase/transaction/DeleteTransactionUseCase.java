package com.debtmap.usecase.transaction;

import com.debtmap.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase {

    private final TransactionRepository transactionRepository;

    @Transactional
    public void execute(UUID transactionId, UUID userId) {
        transactionRepository.findByIdAndUserId(transactionId, userId)
                .ifPresentOrElse(
                        transaction -> {
                            transactionRepository.delete(transaction);
                            log.info("Transação deletada: {}", transactionId);
                        },
                        () -> { throw new IllegalArgumentException("Transação não encontrada"); }
                );
    }
}