package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound_shouldReturnNotFoundResponse() {
        NotFoundException ex = new NotFoundException("Ресурс не найден");
        Map<String, String> response = errorHandler.handleNotFound(ex);

        assertThat(response).containsEntry("error", "Ресурс не найден");
    }

    @Test
    void handleEmailDuplicate_shouldReturnConflictResponse() {
        DuplicateEmailException ex = new DuplicateEmailException("Email уже существует");
        Map<String, String> response = errorHandler.handleEmailDuplicate(ex);

        assertThat(response).containsEntry("error", "Email уже существует");
    }

    @Test
    void handleBadRequest_shouldReturnBadRequestResponse() {
        ValidationException ex = new ValidationException("Некорректные данные");
        Map<String, String> response = errorHandler.handleBadRequest(ex);

        assertThat(response).containsEntry("error", "Некорректные данные");
    }

    @Test
    void handleForbidden_shouldReturnForbiddenResponse() {
        ForbiddenException ex = new ForbiddenException("Доступ запрещен");
        Map<String, String> response = errorHandler.handleForbidden(ex);

        assertThat(response).containsEntry("error", "Доступ запрещен");
    }

    @Test
    void handleThrowable_shouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Что-то пошло не так");
        Map<String, String> response = errorHandler.handleThrowable(ex);

        assertThat(response).containsKey("error");
    }
}