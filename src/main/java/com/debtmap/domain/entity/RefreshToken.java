package com.debtmap.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // usuário dono do token
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // valor do refresh token (string longa gerada pelo JwtService)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    // data de expiração do token
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // true = token invalidado manualmente (logout)
    @Column(nullable = false)
    private Boolean revoked;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (revoked == null) revoked = false;
    }

    // verifica se o token ainda é válido
    public boolean isValid() {
        return !revoked && LocalDateTime.now().isBefore(expiresAt);
    }
}