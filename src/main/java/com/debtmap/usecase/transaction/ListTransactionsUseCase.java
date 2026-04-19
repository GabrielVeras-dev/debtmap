package com.debtmap.usecase.transaction;

import com.debtmap.domain.repository.TransactionRepository;
import com.debtmap.infrastructure.mapper.TransactionMapper;
import com.debtmap.shared.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListTransactionsUseCase {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    public List<TransactionResponse> execute(UUID userId) {
        return transactionRepository
                .findByUserIdOrderByTransactionDateDesc(userId)
                .stream()
                .map(transactionMapper::toResponse)
                .toList();
    }
}