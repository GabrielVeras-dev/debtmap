package com.debtmap.usecase.auth;

import com.debtmap.domain.entity.User;
import com.debtmap.domain.repository.UserRepository;
import com.debtmap.infrastructure.security.JwtService;
import com.debtmap.shared.dto.request.LoginRequest;
import com.debtmap.shared.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse execute(LoginRequest request) {

        // delega a validação de email/senha ao Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("Login realizado: {}", user.getEmail());

        return new AuthResponse(accessToken, refreshToken, user.getName(), user.getEmail());
    }
}