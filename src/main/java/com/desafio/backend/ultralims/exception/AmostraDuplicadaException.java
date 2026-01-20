package com.desafio.backend.ultralims.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class AmostraDuplicadaException extends RuntimeException{
     private HttpStatus status;

    public AmostraDuplicadaException(String codAmostra){
        super(codAmostra + " já esta cadastrado.");
        this.status = HttpStatus.CONFLICT;
    }
}
