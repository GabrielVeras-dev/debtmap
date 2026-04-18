package com.debtmap.usecase.auth;

import com.debtmap.domain.entity.User;
import com.debtmap.domain.repository.UserRepository;
import com.debtmap.infrastructure.security.JwtService;
import com.debtmap.shared.dto.request.RefreshTokenRequest;
import com.debtmap.shared.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthResponse execute(RefreshTokenRequest request) {

        // extrai o email do refresh token
        String email = jwtService.extractUsername(request.refreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // valida o refresh token
        if (!jwtService.isTokenValid(request.refreshToken(), user)) {
            throw new IllegalArgumentException("Refresh token inválido ou expirado");
        }

        // gera novo access token
        String newAccessToken = jwtService.generateAccessToken(user);

        log.info("Token renovado para: {}", email);

        return new AuthResponse(newAccessToken, request.refreshToken(), user.getName(), user.getEmail());
    }
}