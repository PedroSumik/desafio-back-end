package com.desafio.backend.ultralims.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrocaStatusInvalidException extends RuntimeException {
    private HttpStatus status;

    public TrocaStatusInvalidException(String mensagem) {
        super(mensagem);
        this.status = HttpStatus.NOT_FOUND;
    }
}
