package com.debtmap.usecase.transaction;

import com.debtmap.domain.entity.Category;
import com.debtmap.domain.entity.Transaction;
import com.debtmap.domain.repository.CategoryRepository;
import com.debtmap.domain.repository.TransactionRepository;
import com.debtmap.infrastructure.mapper.TransactionMapper;
import com.debtmap.shared.dto.request.TransactionRequest;
import com.debtmap.shared.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse execute(UUID transactionId, TransactionRequest request, UUID userId) {

        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada"));

        // atualiza categoria se informada
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.categoryId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        }

        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setNotes(request.notes());
        transaction.setCategory(category);

        transactionRepository.save(transaction);
        log.info("Transação atualizada: {}", transactionId);

        return transactionMapper.toResponse(transaction);
    }
}