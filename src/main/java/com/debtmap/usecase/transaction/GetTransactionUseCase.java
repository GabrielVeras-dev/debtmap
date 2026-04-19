package com.debtmap.usecase.transaction;

import com.debtmap.domain.repository.TransactionRepository;
import com.debtmap.infrastructure.mapper.TransactionMapper;
import com.debtmap.shared.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    public TransactionResponse execute(UUID transactionId, UUID userId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .map(transactionMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada"));
    }
}