package com.example.demo.DTO;

/**
 * Тело ответа при ошибке: {"error": "текст"}. Формируется в GlobalExceptionHandler.
 */
public record ErrorResponse(String error) {}
