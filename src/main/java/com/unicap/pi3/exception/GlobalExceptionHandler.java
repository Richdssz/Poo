
package com.unicap.pi3.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ERROS DE VALIDAÇÃO - HTTP 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(
            MethodArgumentNotValidException erro) {

        String mensagem = erro.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Dados inválidos.");

        return ResponseEntity.badRequest()
                .body(Map.of("erro", mensagem));
    }

    // RECURSO NÃO ENCONTRADO - HTTP 404
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(
            RecursoNaoEncontradoException erro) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", erro.getMessage()));
    }

    // DADOS INVÁLIDOS - HTTP 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarArgumentoInvalido(
            IllegalArgumentException erro) {

        return ResponseEntity.badRequest()
                .body(Map.of("erro", erro.getMessage()));
    }

    // DUPLICIDADE - HTTP 409
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Map<String, String>> tratarConflito(
            ConflitoException erro) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erro", erro.getMessage()));
    }

    // VIOLAÇÃO DE REGRAS DO BANCO - HTTP 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarIntegridade(
            DataIntegrityViolationException erro) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "erro",
                        "Os dados informados já existem ou violam uma regra do banco."
                ));
    }

    // ACESSO NEGADO - HTTP 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> tratarAcessoNegado(
            AccessDeniedException erro) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("erro", erro.getMessage()));
    }

    // ERRO INTERNO - HTTP 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarErroGeral(
            Exception erro) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "erro",
                        "Ocorreu um erro interno no servidor."
                ));
    }
}
