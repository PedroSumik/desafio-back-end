package com.desafio.backend.ultralims.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "amostra_status_historico")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmostraStatusHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amostra_id", nullable = false)
    @JsonBackReference
    private Amostra codAmostra;

    @Enumerated(EnumType.STRING)
    private StatusAmostra codStatus;

    @UpdateTimestamp
    private LocalDateTime dataAlteracao;

    public AmostraStatusHistorico(Amostra codAmostra, StatusAmostra codStatus, LocalDateTime dataAlteracao) {
        this.codAmostra = codAmostra;
        this.codStatus = codStatus;
        this.dataAlteracao = dataAlteracao;
    }
}
