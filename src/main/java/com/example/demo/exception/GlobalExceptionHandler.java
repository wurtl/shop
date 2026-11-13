package com.example.demo.exception;

import com.example.demo.DTO.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Единое место превращения исключений в ответы {"error": "..."} — вместо try/catch в каждом контроллере. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * NotFoundException → HTTP 404 и тело {"error": "..."}.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * BusinessException → HTTP 400 и тело {"error": "..."}.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(BusinessException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Нарушено ограничение базы данных → HTTP 400.
     * Типичный случай: удаляем товар, категорию или пользователя, на которые уже ссылаются
     * заказы, корзины или избранное.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse dataIntegrity(DataIntegrityViolationException e) {
        return new ErrorResponse("Операция невозможна: запись используется в других данных или поля заполнены неверно");
    }
}
