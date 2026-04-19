package com.debtmap.usecase.transaction;

import com.debtmap.domain.entity.Category;
import com.debtmap.domain.entity.Transaction;
import com.debtmap.domain.entity.User;
import com.debtmap.domain.repository.CategoryRepository;
import com.debtmap.domain.repository.TransactionRepository;
import com.debtmap.domain.repository.UserRepository;
import com.debtmap.infrastructure.mapper.TransactionMapper;
import com.debtmap.shared.dto.request.TransactionRequest;
import com.debtmap.shared.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse execute(TransactionRequest request, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // busca a categoria se informada
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.categoryId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .type(request.type())
                .amount(request.amount())
                .description(request.description())
                .transactionDate(request.transactionDate())
                .notes(request.notes())
                .build();

        transactionRepository.save(transaction);
        log.info("Transação criada: {} - {} - {}", transaction.getType(), transaction.getAmount(), userId);

        return transactionMapper.toResponse(transaction);
    }
}