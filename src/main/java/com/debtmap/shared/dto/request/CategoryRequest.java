package com.debtmap.shared.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 80, message = "Nome deve ter entre 2 e 80 caracteres")
        String name,

        // cor em hexadecimal — opcional
        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Cor deve estar no formato hexadecimal (#RRGGBB)")
        String color,

        // nome do ícone — opcional
        @Size(max = 50, message = "Ícone deve ter no máximo 50 caracteres")
        String icon
) {}