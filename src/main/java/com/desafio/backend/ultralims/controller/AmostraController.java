package com.desafio.backend.ultralims.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.desafio.backend.ultralims.entity.Amostra;
import com.desafio.backend.ultralims.entity.StatusAmostra;
import com.desafio.backend.ultralims.request.AmostraRequest;
import com.desafio.backend.ultralims.response.AmostraDetalheResponse;
import com.desafio.backend.ultralims.response.AmostraResponse;
import com.desafio.backend.ultralims.service.AmostraService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/amostras")
public class AmostraController {

    @Autowired
    private AmostraService amostraService;
    
    @PostMapping
    public ResponseEntity<Amostra> criarAmostra(@Valid @RequestBody AmostraRequest amostraRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(amostraService.criaAmostra(amostraRequest));
    }

    @GetMapping
    public ResponseEntity<List<AmostraResponse>> listarTodasAmostras(
            @RequestParam(required = false) String codAmostra,
            @RequestParam(required = false) StatusAmostra status,
            @RequestParam(required = false) LocalDateTime inicio,
            @RequestParam(required = false) LocalDateTime fim
        ) {
        return ResponseEntity.status(HttpStatus.OK).body(amostraService.listarTodasAmostras(codAmostra, status, inicio, fim));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmostraDetalheResponse> getAmostraById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(amostraService.getAmostraById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Amostra> atualizarAmostra(@RequestBody AmostraRequest amostraRequest){
        return ResponseEntity.status(HttpStatus.OK).body(amostraService.atualizarAmostra(amostraRequest));
    }


    @PutMapping("/atualizar/{id}")
    public ResponseEntity<String> atualizarStatusAmostra(@PathVariable UUID id) {
        Amostra amostraAtualizada = amostraService.atualizarStatusAmostra(id);
        return ResponseEntity.status(HttpStatus.OK).body("Status da amostra "+ amostraAtualizada.getCodAmostra() + " atualizado para " + amostraAtualizada.getStatus() + " com sucesso!");
    }

    @PutMapping("/rejeitar/{id}")
    public ResponseEntity<String> rejeitarStatusAmostra(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body("Amostra rejeitada: " + amostraService.rejeitarAmostra(id).getCodAmostra());
    }

    @PutMapping("/aprovar/{id}")
    public ResponseEntity<String> aprovarStatusAmostra(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body("Amostra " + amostraService.aprovarAmostra(id).getCodAmostra() + " aprovada com sucesso!");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Amostra> deletarAmostra(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(amostraService.deletarAmostra(id));
    }
}
