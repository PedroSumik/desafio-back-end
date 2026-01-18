package com.desafio.backend.ultralims.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.desafio.backend.ultralims.entity.StatusAmostra;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmostraResponse {
    private UUID id;

    private String codAmostra;

    private String tipoColeta;

    private LocalDateTime dataColeta;

    private StatusAmostra status;
}
