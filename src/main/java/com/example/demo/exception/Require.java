package com.example.demo.exception;

/**
 * Проверки входных данных в одном месте: при нарушении бросают BusinessException (HTTP 400).
 * Нужны, чтобы некорректный запрос (пустое значение, отрицательное количество)
 * получал понятный ответ, а не ошибку 500 из глубины приложения.
 */
public final class Require {

    private Require() {
    }

    /** Строка не null и не состоит из одних пробелов. */
    public static void notBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(message);
        }
    }

    /** Значение указано. */
    public static void notNull(Object value, String message) {
        if (value == null) {
            throw new BusinessException(message);
        }
    }

    /** Число указано и больше нуля. */
    public static void positive(Number value, String message) {
        if (value == null || value.doubleValue() <= 0) {
            throw new BusinessException(message);
        }
    }

    /** Число указано и не меньше нуля. */
    public static void nonNegative(Number value, String message) {
        if (value == null || value.doubleValue() < 0) {
            throw new BusinessException(message);
        }
    }
}
