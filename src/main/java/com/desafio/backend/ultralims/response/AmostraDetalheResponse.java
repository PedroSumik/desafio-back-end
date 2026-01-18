package com.desafio.backend.ultralims.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.desafio.backend.ultralims.entity.AmostraStatusHistorico;
import com.desafio.backend.ultralims.entity.StatusAmostra;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmostraDetalheResponse {
    private UUID id;

    private String codAmostra;

    private String tipoColeta;

    private LocalDateTime dataColeta;

    private StatusAmostra status;

    private List<AmostraStatusHistorico> historicoStatus;
}
