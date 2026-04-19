package com.debtmap.usecase.category;

import com.debtmap.domain.entity.Category;
import com.debtmap.domain.repository.CategoryRepository;
import com.debtmap.infrastructure.mapper.CategoryMapper;
import com.debtmap.shared.dto.request.CategoryRequest;
import com.debtmap.shared.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse execute(UUID categoryId, CategoryRequest request, UUID userId) {

        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        // verifica duplicidade de nome apenas se o nome mudou
        if (!category.getName().equals(request.name()) &&
                categoryRepository.existsByUserIdAndName(userId, request.name())) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + request.name());
        }

        category.setName(request.name());
        category.setColor(request.color());
        category.setIcon(request.icon());

        categoryRepository.save(category);
        log.info("Categoria atualizada: {}", categoryId);

        return categoryMapper.toResponse(category);
    }
}