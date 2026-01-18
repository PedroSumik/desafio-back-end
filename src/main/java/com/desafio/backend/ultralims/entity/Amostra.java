package com.desafio.backend.ultralims.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "amostras")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Amostra{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cod_amostra", unique = true, nullable = false)
    private String codAmostra;

    @Column(name = "tipo_coleta", nullable = false)
    private String tipoColeta;

    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataColeta;

    @Enumerated(EnumType.STRING)
    private StatusAmostra status;

    @OneToMany(mappedBy = "codAmostra", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AmostraStatusHistorico> historicoStatus;

    public Amostra(String codAmostra, String tipoColeta, LocalDateTime dataColeta) {
        this.codAmostra = codAmostra;
        this.tipoColeta = tipoColeta;
        this.dataColeta = dataColeta;
        this.status = StatusAmostra.PENDENTE;
    }

    public void avancarStatus() {
        this.status = this.status.avancarStatus();
    }

    public void rejeitarAmostra() {
        this.status = this.status.rejeitarAmostra();
    }

    public void aprovarAmostra() {
        this.status = this.status.aprovarAmostra();
    }
}
