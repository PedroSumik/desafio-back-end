package com.desafio.backend.ultralims.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.desafio.backend.ultralims.entity.Amostra;
import com.desafio.backend.ultralims.entity.AmostraStatusHistorico;
import com.desafio.backend.ultralims.entity.StatusAmostra;
import com.desafio.backend.ultralims.exception.AmostraDuplicadaException;
import com.desafio.backend.ultralims.exception.AmostraNotFoundException;
import com.desafio.backend.ultralims.mapper.AmostraMapper;
import com.desafio.backend.ultralims.repository.AmostraRepository;
import com.desafio.backend.ultralims.repository.AmostraStatusHistoricoRepository;
import com.desafio.backend.ultralims.repository.specification.AmostraSpecification;
import com.desafio.backend.ultralims.request.AmostraRequest;
import com.desafio.backend.ultralims.request.AtualizarAmostraRequest;
import com.desafio.backend.ultralims.response.AmostraDetalheResponse;
import com.desafio.backend.ultralims.response.AmostraResponse;

import jakarta.transaction.Transactional;

@Service
public class AmostraService {

    @Autowired
    private AmostraRepository amostraRepository;

    @Autowired
    private AmostraMapper amostraMapper;

    @Autowired
    private AmostraStatusHistoricoRepository amostraStatusHistoricoRepository;

    @Transactional
    public Amostra criaAmostra(AmostraRequest amostraRequest) {
        if (amostraRepository.findByCodAmostra(amostraRequest.getCodAmostra()).isPresent()) {
            throw new AmostraDuplicadaException("Amostra " + amostraRequest.getCodAmostra() + " ja esta cadastrada.");
        }

        verificaValoresRequest(amostraRequest);

        Amostra novaAmostra = amostraRepository.save(amostraMapper.AmostraRequestToAmostra(amostraRequest));
        amostraStatusHistoricoRepository.save(new AmostraStatusHistorico(novaAmostra, novaAmostra.getStatus(), LocalDateTime.now()));
        return novaAmostra;
    }

    @Transactional
    public Page<AmostraResponse> listarTodasAmostras(String codAmostra, StatusAmostra status, LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {

        Specification<Amostra> specification = Specification.
                                                where(AmostraSpecification.porCodigo(codAmostra))
                                                .and(AmostraSpecification.porStatus(status))
                                                .and(AmostraSpecification.porData(inicio, fim));

        return amostraRepository.findAll(specification, pageable)
                    .map( a -> amostraMapper.AmostraToAmostraResponse(a));
    }

    @Transactional
    public AmostraDetalheResponse getAmostraById(UUID id) {
        return amostraRepository.findById(id)
                    .map( a -> amostraMapper.AmostraToAmostraDetalheResponse(a))
                                .orElseThrow(() -> new AmostraNotFoundException("Amostra com id " + id + " não foi encontrada"));
    }

    @Transactional
    public String atualizarAmostra( UUID id, AtualizarAmostraRequest amostraRequest){
        if (amostraRequest.getDataColeta() != null && amostraRequest.getDataColeta().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data de coleta nao pode ser futura ao dia atual.");
        }

        Amostra amostra = buscaAmostra(id);
        amostraMapper.AmostraAtualizarRequestToAmostra(amostra, amostraRequest);
        amostraRepository.save(amostra);
        return "Amostra atualizada com sucesso!";
    }

    @Transactional
    public String atualizarStatusAmostra(UUID id) {
        Amostra amostra = buscaAmostra(id);
        amostra.setStatus(amostra.getStatus().avancarStatus());
        amostraStatusHistoricoRepository.save(new AmostraStatusHistorico(amostra, amostra.getStatus(), LocalDateTime.now()));
        amostraRepository.save(amostra);
        return "Status da amostra "+ amostra.getCodAmostra() + " atualizado para " + amostra.getStatus() + " com sucesso!";
    }

    @Transactional
    public String rejeitarAmostra(UUID id) {
        Amostra amostra = buscaAmostra(id);
        amostra.setStatus(amostra.getStatus().rejeitarAmostra());
        amostraRepository.save(amostra);
        amostraStatusHistoricoRepository.save(new AmostraStatusHistorico(amostra, amostra.getStatus(), LocalDateTime.now()));
        return "Amostra rejeitada: " + amostra.getCodAmostra();
    }

    @Transactional
    public String aprovarAmostra(UUID id) {
        Amostra amostra = buscaAmostra(id);
        amostra.setStatus(amostra.getStatus().aprovarAmostra());
        amostraRepository.save(amostra);
        amostraStatusHistoricoRepository.save(new AmostraStatusHistorico(amostra, amostra.getStatus(), LocalDateTime.now()));
        return "Amostra " + amostra.getCodAmostra() + " aprovada com sucesso!";
    }

    @Transactional
    public String deletarAmostra(UUID id) {
        Amostra amostra = buscaAmostra(id);
        amostraRepository.deleteById(amostra.getId());
        return "Amostra deletada com sucesso!";
    }

    private Amostra buscaAmostra(UUID id){
        return amostraRepository.findById(id).orElseThrow((() -> new AmostraNotFoundException("Amostra com id " + id + " não foi encontrada")));
    }

    private void verificaValoresRequest(AmostraRequest amostraRequest){
        if (amostraRequest.getCodAmostra() == null || amostraRequest.getCodAmostra() == "") {
            throw new IllegalArgumentException("Código de amostra deve possuir um valor.");
        }

        if (amostraRequest.getTipoColeta() == null || amostraRequest.getCodAmostra() == "") {
            throw new IllegalArgumentException("Tipo de coleta deve possuir um valor.");
        }

        if (amostraRequest.getDataColeta() == null || amostraRequest.getDataColeta().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data de coleta nao pode ser futura ao dia atual.");
        }
    }
}
