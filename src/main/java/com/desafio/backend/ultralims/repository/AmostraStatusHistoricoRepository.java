package com.desafio.backend.ultralims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.desafio.backend.ultralims.entity.AmostraStatusHistorico;

@Repository
public interface AmostraStatusHistoricoRepository extends JpaRepository<AmostraStatusHistorico, Long> {
}
