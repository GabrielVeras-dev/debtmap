package com.debtmap.usecase.category;

import com.debtmap.domain.entity.Category;
import com.debtmap.domain.entity.User;
import com.debtmap.domain.repository.CategoryRepository;
import com.debtmap.domain.repository.UserRepository;
import com.debtmap.infrastructure.mapper.CategoryMapper;
import com.debtmap.shared.dto.request.CategoryRequest;
import com.debtmap.shared.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse execute(CategoryRequest request, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // verifica se já existe categoria com o mesmo nome para este usuário
        if (categoryRepository.existsByUserIdAndName(userId, request.name())) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + request.name());
        }

        Category category = Category.builder()
                .user(user)
                .name(request.name())
                .color(request.color())
                .icon(request.icon())
                .build();

        categoryRepository.save(category);
        log.info("Categoria criada: {} para usuário {}", category.getName(), userId);

        return categoryMapper.toResponse(category);
    }
}