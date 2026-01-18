package com.desafio.backend.ultralims.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.desafio.backend.ultralims.entity.Amostra;
import com.desafio.backend.ultralims.entity.AmostraStatusHistorico;
import com.desafio.backend.ultralims.entity.StatusAmostra;
import com.desafio.backend.ultralims.exception.AmostraDuplicadaException;
import com.desafio.backend.ultralims.exception.AmostraNotFoundException;
import com.desafio.backend.ultralims.repository.AmostraRepository;
import com.desafio.backend.ultralims.repository.AmostraStatusHistoricoRepository;
import com.desafio.backend.ultralims.repository.specification.AmostraSpecification;
import com.desafio.backend.ultralims.request.AmostraRequest;
import com.desafio.backend.ultralims.response.AmostraDetalheResponse;
import com.desafio.backend.ultralims.response.AmostraResponse;

import jakarta.transaction.Transactional;

@Service
public class AmostraService {

    @Autowired
    private AmostraRepository amostraRepository;


    @Autowired
    private AmostraStatusHistoricoRepository amostraStatusHistoricoRepository;


    @Transactional
    public Amostra criaAmostra(AmostraRequest amostraRequest) {
        if (amostraRepository.findByCodAmostra(amostraRequest.getCodAmostra()).isPresent()) {
            throw new AmostraDuplicadaException(amostraRequest.getCodAmostra());
        }

        Amostra novaAmostra = amostraRepository.save(amostraRequest.toObject());
        amostraStatusHistoricoRepository.save(new AmostraStatusHistorico(novaAmostra, novaAmostra.getStatus(), LocalDateTime.now()));
        return novaAmostra;
    }

    @Transactional
    public List<AmostraResponse> listarTodasAmostras(String codAmostra, StatusAmostra status, LocalDateTime inicio, LocalDateTime fim) {

        Specification<Amostra> specification = Specification.
                                                where(AmostraSpecification.porCodigo(codAmostra))
                                                .and(AmostraSpecification.porStatus(status))
                                                .and(AmostraSpecification.porData(inicio, fim));

        return amostraRepository.findAll(specification)
                .stream()
                .map( a -> new AmostraResponse(
                    a.getId(),
                    a.getCodAmostra(),
                    a.getTipoColeta(),
                    a.getDataColeta(),
                    a.getStatus()
                )).toList();
    }

    public AmostraDetalheResponse getAmostraById(UUID id) {
        return amostraRepository.findById(id)
                    .map( a -> new AmostraDetalheResponse(
                        a.getId(),
                        a.getCodAmostra(),
                        a.getTipoColeta(),
                        a.getDataColeta(),
                        a.getStatus(),
                        a.getHistoricoStatus()
                    )).orElseThrow(() -> new AmostraNotFoundException(id));
    }

    @Transactional
    public Amostra atualizarStatusAmostra(UUID id) {
        Amostra amostra = buscAmostra(id);
        amostra.avancarStatus();
        amostraStatusHistoricoRepository.save(new AmostraStatusHistorico(amostra, amostra.getStatus(), LocalDateTime.now()));
        amostraRepository.save(amostra);
        return amostra;
    }

    @Transactional
    public Amostra rejeitarAmostra(UUID id) {
        Amostra amostra = buscAmostra(id);
        amostra.rejeitarAmostra();
        amostraRepository.save(amostra);
        amostraStatusHistoricoRepository.save(new AmostraStatusHistorico(amostra, amostra.getStatus(), LocalDateTime.now()));
        return amostra;
    }

    @Transactional
    public Amostra aprovarAmostra(UUID id) {
        Amostra amostra = buscAmostra(id);
        amostra.aprovarAmostra();
        amostraRepository.save(amostra);
        amostraStatusHistoricoRepository.save(new AmostraStatusHistorico(amostra, amostra.getStatus(), LocalDateTime.now()));
        return amostra;
    }

    @Transactional
    public Amostra deletarAmostra(UUID id) {
        Amostra amostra = buscAmostra(id);
        amostraRepository.deleteById(amostra.getId());
        return amostra;
    }


    private Amostra buscAmostra(UUID id){
        return amostraRepository.findById(id).orElseThrow((() -> new AmostraNotFoundException(id)));
    }
}
