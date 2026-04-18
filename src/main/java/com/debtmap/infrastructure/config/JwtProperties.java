package com.debtmap.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// lê automaticamente o bloco debtmap.security.jwt do application.yml
@ConfigurationProperties(prefix = "debtmap.security.jwt")
public record JwtProperties(
        String secret,
        Long accessTokenExpiration,
        Long refreshTokenExpiration
) {}