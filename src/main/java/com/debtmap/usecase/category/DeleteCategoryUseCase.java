package com.debtmap.usecase.category;

import com.debtmap.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void execute(UUID categoryId, UUID userId) {
        categoryRepository.findByIdAndUserId(categoryId, userId)
                .ifPresentOrElse(
                        category -> {
                            categoryRepository.delete(category);
                            log.info("Categoria deletada: {}", categoryId);
                        },
                        () -> { throw new IllegalArgumentException("Categoria não encontrada"); }
                );
    }
}