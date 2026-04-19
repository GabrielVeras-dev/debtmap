package com.debtmap.controller;

import com.debtmap.domain.entity.User;
import com.debtmap.shared.dto.request.DebtRequest;
import com.debtmap.shared.dto.request.PayInstallmentRequest;
import com.debtmap.shared.dto.response.DebtResponse;
import com.debtmap.shared.dto.response.InstallmentResponse;
import com.debtmap.usecase.debt.*;
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
@RequestMapping("/api/debts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Dívidas", description = "Controle de dívidas com parcelas e cálculo de juros")
public class DebtController {

    private final CreateDebtUseCase createDebtUseCase;
    private final ListDebtsUseCase listDebtsUseCase;
    private final GetDebtUseCase getDebtUseCase;
    private final PayInstallmentUseCase payInstallmentUseCase;
    private final DeleteDebtUseCase deleteDebtUseCase;

    @PostMapping
    @Operation(summary = "Cria uma nova dívida com parcelas geradas automaticamente")
    public ResponseEntity<DebtResponse> create(
            @Valid @RequestBody DebtRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createDebtUseCase.execute(request, user.getId()));
    }

    @GetMapping
    @Operation(summary = "Lista todas as dívidas do usuário")
    public ResponseEntity<List<DebtResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(listDebtsUseCase.execute(user.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma dívida pelo ID com todas as parcelas")
    public ResponseEntity<DebtResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(getDebtUseCase.execute(id, user.getId()));
    }

    @PostMapping("/{debtId}/installments/{installmentId}/pay")
    @Operation(summary = "Registra o pagamento de uma parcela")
    public ResponseEntity<InstallmentResponse> payInstallment(
            @PathVariable UUID debtId,
            @PathVariable UUID installmentId,
            @Valid @RequestBody PayInstallmentRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                payInstallmentUseCase.execute(debtId, installmentId, request, user.getId())
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma dívida e todas as suas parcelas")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        deleteDebtUseCase.execute(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}