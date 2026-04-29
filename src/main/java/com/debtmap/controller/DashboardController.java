package com.debtmap.controller;

import com.debtmap.domain.entity.User;
import com.debtmap.shared.dto.response.DashboardResponse;
import com.debtmap.usecase.dashboard.GetDashboardUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Dashboard", description = "Resumo financeiro do usuário")
public class DashboardController {

    private final GetDashboardUseCase getDashboardUseCase;

    @GetMapping
    @Operation(summary = "Retorna o resumo financeiro do mês atual")
    public ResponseEntity<DashboardResponse> getDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(getDashboardUseCase.execute(user.getId()));
    }
}