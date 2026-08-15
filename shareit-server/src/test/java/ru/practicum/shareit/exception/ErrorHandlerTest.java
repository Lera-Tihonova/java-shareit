package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound_shouldReturnNotFoundResponse() {
        NotFoundException ex = new NotFoundException("Ресурс не найден");
        ErrorResponse response = errorHandler.handleNotFound(ex);

        assertThat(response.getError()).isEqualTo("Not Found");
        assertThat(response.getDescription()).isEqualTo("Ресурс не найден");
    }

    @Test
    void handleDuplicateEmail_shouldReturnConflictResponse() {
        DuplicateEmailException ex = new DuplicateEmailException("Email уже существует");
        ErrorResponse response = errorHandler.handleDuplicateEmail(ex);

        assertThat(response.getError()).isEqualTo("Conflict");
        assertThat(response.getDescription()).isEqualTo("Email уже существует");
    }

    @Test
    void handleValidation_shouldReturnBadRequestResponse() {
        ValidationException ex = new ValidationException("Некорректные данные");
        ErrorResponse response = errorHandler.handleValidation(ex);

        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getDescription()).isEqualTo("Некорректные данные");
    }

    @Test
    void handleForbidden_shouldReturnForbiddenResponse() {
        ForbiddenException ex = new ForbiddenException("Доступ запрещен");
        ErrorResponse response = errorHandler.handleForbidden(ex);

        assertThat(response.getError()).isEqualTo("Forbidden");
        assertThat(response.getDescription()).isEqualTo("Доступ запрещен");
    }

    @Test
    void handleRuntimeException_shouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Что-то пошло не так");
        ErrorResponse response = errorHandler.handleRuntimeException(ex);

        assertThat(response.getError()).isEqualTo("Internal Server Error");
        assertThat(response.getDescription()).contains("Произошла непредвиденная ошибка");
    }
}