package com.desafio.backend.ultralims.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmostraNotFoundException extends RuntimeException{
    private HttpStatus status;

    public AmostraNotFoundException(UUID id){
        super("Não foi encontrado a amostra com id: " + id);
        this.status = HttpStatus.NOT_FOUND;
    }
}
