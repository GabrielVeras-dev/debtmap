package com.debtmap.usecase.auth;

import com.debtmap.domain.entity.User;
import com.debtmap.domain.enums.UserRole;
import com.debtmap.domain.repository.UserRepository;
import com.debtmap.infrastructure.security.JwtService;
import com.debtmap.shared.dto.request.RegisterRequest;
import com.debtmap.shared.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse execute(RegisterRequest request) {

        // verifica se o email já está em uso
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email já cadastrado: " + request.email());
        }

        // cria e persiste o novo usuário
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .active(true)
                .build();

        userRepository.save(user);
        log.info("Novo usuário registrado: {}", user.getEmail());

        // gera tokens e retorna
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, user.getName(), user.getEmail());
    }
}