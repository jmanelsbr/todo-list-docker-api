package com.example.demo.exception;

import com.example.demo.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
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

    @ExceptionHandler
    public ResponseEntity NoSuchElementException(NoSuchElementException e, HttpServletRequest request) {
        ApiErrorResponse notFound = new ApiErrorResponse(LocalDateTime.now(), 404, "Not Found", "Elemento não encontrado", request.getRequestURI());
        return new ResponseEntity(notFound, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    public ResponseEntity HttpRequestMethodNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        ApiErrorResponse NotValid = new ApiErrorResponse(LocalDateTime.now(), 400, "Bad Request", "A requisição contém dados inválidos", request.getRequestURI());
        return new ResponseEntity(NotValid, HttpStatus.BAD_REQUEST);
    }

}
