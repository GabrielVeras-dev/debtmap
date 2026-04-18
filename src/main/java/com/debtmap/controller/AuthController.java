package com.debtmap.controller;

import com.debtmap.shared.dto.request.LoginRequest;
import com.debtmap.shared.dto.request.RefreshTokenRequest;
import com.debtmap.shared.dto.request.RegisterRequest;
import com.debtmap.shared.dto.response.AuthResponse;
import com.debtmap.usecase.auth.LoginUseCase;
import com.debtmap.usecase.auth.RefreshTokenUseCase;
import com.debtmap.usecase.auth.RegisterUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de registro, login e renovação de token")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerUseCase.execute(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica um usuário e retorna os tokens JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.execute(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renova o access token usando o refresh token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenUseCase.execute(request));
    }
}