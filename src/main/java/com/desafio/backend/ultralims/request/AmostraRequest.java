package com.desafio.backend.ultralims.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.desafio.backend.ultralims.entity.Amostra;
import com.desafio.backend.ultralims.entity.StatusAmostra;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmostraRequest {
    private UUID id;

    @NotBlank(message = "Código de amostra é obrigatório")
    @NotNull(message = "Código de amostra é obrigatório")
    private String codAmostra;

    @NotBlank(message = "Tipo de coleta é obrigatório")
    @NotNull(message = "Tipo de coleta é obrigatório")
    private String tipoColeta;

    @NotBlank(message = "Data é obrigatório")
    @NotNull(message = "Data é obrigatório")
    private LocalDateTime dataColeta;

    @Null
    private StatusAmostra status;

    public Amostra toObject() {
        return new Amostra(codAmostra, tipoColeta, dataColeta);
    }
}
