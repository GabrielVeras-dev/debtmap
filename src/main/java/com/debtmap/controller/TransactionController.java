package com.debtmap.controller;

import com.debtmap.domain.entity.User;
import com.debtmap.shared.dto.request.TransactionRequest;
import com.debtmap.shared.dto.response.TransactionResponse;
import com.debtmap.usecase.transaction.*;
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
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Transações", description = "CRUD de transações financeiras")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    @PostMapping
    @Operation(summary = "Cria uma nova transação")
    public ResponseEntity<TransactionResponse> create(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createTransactionUseCase.execute(request, user.getId()));
    }

    @GetMapping
    @Operation(summary = "Lista todas as transações do usuário")
    public ResponseEntity<List<TransactionResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(listTransactionsUseCase.execute(user.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma transação pelo ID")
    public ResponseEntity<TransactionResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(getTransactionUseCase.execute(id, user.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma transação")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(updateTransactionUseCase.execute(id, request, user.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma transação")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        deleteTransactionUseCase.execute(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}