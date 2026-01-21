package com.desafio.backend.ultralims.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmostraNotFoundException extends RuntimeException{
    private HttpStatus status;

    public AmostraNotFoundException(String message){
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }
}
