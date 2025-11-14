package com.example.demo.exception;

import com.example.demo.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExcetionHandler {

    private final static Logger logger =  LoggerFactory.getLogger(GlobalExcetionHandler.class);

    @ExceptionHandler
    public ResponseEntity NoSuchElementException(NoSuchElementException e, HttpServletRequest request) {

        logger.warn("Tentativa de acesso a recurso não encontrado (404). Path: {}", request.getRequestURI());

        ApiErrorResponse notFound = new ApiErrorResponse(LocalDateTime.now(), 404, "Not Found", "Elemento não encontrado", request.getRequestURI());
        return new ResponseEntity(notFound, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    public ResponseEntity HttpRequestMethodNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {

        logger.warn("Falha na validação (400) no path: {}. Erro: {}", request.getRequestURI(), e.getMessage());

        ApiErrorResponse NotValid = new ApiErrorResponse(LocalDateTime.now(), 400, "Bad Request", "A requisição contém dados inválidos", request.getRequestURI());
        return new ResponseEntity(NotValid, HttpStatus.BAD_REQUEST);
    }

}
