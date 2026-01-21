package com.desafio.backend.ultralims.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AmostraNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAmostraNotFound(AmostraNotFoundException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(
                "Amostra nao encontrada",
                e.getMessage()));
    }

    @ExceptionHandler(AmostraDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleAmostraDuplicada(AmostraDuplicadaException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(
                "Amostra duplicada",
                e.getMessage()));
    }

    @ExceptionHandler(TrocaStatusInvalidException.class)
    public ResponseEntity<ErrorResponse> handleCaminhoStatusInvalido(TrocaStatusInvalidException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new ErrorResponse(
                        "Caminho de status invalido",
                        e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleValidacao(MethodArgumentNotValidException e) {
        List<ErrorResponse> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ex -> new ErrorResponse(ex.getField(), ex.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(e.getStatusCode()).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "Erro Interno",
                        e.getMessage()));
    }
}
