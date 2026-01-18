package com.desafio.backend.ultralims.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.desafio.backend.ultralims.entity.Amostra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AmostraRepository extends JpaRepository<Amostra, UUID>, JpaSpecificationExecutor<Amostra> {

    List<Amostra> findAll();

    @EntityGraph(attributePaths = {"historicoStatus"})
    Optional<Amostra> findById(UUID id);

    Optional<Amostra> findByCodAmostra(String codAmostra);
}
