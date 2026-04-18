package com.debtmap.shared.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String name,
        String email
) {
    // construtor auxiliar com tokenType padrão
    public AuthResponse(String accessToken, String refreshToken, String name, String email) {
        this(accessToken, refreshToken, "Bearer", name, email);
    }
}