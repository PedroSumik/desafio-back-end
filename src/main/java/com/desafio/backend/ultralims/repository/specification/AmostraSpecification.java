package com.desafio.backend.ultralims.repository.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.desafio.backend.ultralims.entity.Amostra;
import com.desafio.backend.ultralims.entity.StatusAmostra;

public class AmostraSpecification {
    public static Specification<Amostra> porCodigo(String codAmostra) {
        return (root, query, cb) -> codAmostra == null ? null
                : cb.like(cb.lower(root.get("codAmostra")), "%" + codAmostra.toLowerCase() + "%");
    }

    public static Specification<Amostra> porStatus(StatusAmostra status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Amostra> porData(LocalDateTime inicio, LocalDateTime fim) {
        return (root, query, cb) -> {
            if (inicio == null || fim == null)
                return null;
            return cb.between(root.get("dataColeta"), inicio, fim);
        };
    }

}
