package com.debtmap.shared.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String color,
        String icon,
        LocalDateTime createdAt
) {}