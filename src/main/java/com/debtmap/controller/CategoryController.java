package com.debtmap.controller;

import com.debtmap.domain.entity.User;
import com.debtmap.shared.dto.request.CategoryRequest;
import com.debtmap.shared.dto.response.CategoryResponse;
import com.debtmap.usecase.category.CreateCategoryUseCase;
import com.debtmap.usecase.category.DeleteCategoryUseCase;
import com.debtmap.usecase.category.ListCategoriesUseCase;
import com.debtmap.usecase.category.UpdateCategoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Categorias", description = "Gerenciamento de categorias de gastos")
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    @PostMapping
    @Operation(summary = "Cria uma nova categoria")
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createCategoryUseCase.execute(request, user.getId()));
    }

    @GetMapping
    @Operation(summary = "Lista todas as categorias do usuário")
    public ResponseEntity<List<CategoryResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(listCategoriesUseCase.execute(user.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(updateCategoryUseCase.execute(id, request, user.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma categoria")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        deleteCategoryUseCase.execute(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}