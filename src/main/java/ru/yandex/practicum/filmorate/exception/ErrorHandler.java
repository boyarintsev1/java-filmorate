package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice(value = "ru.yandex.practicum.filmorate.controller")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectIdException(final IncorrectIdException e) {
        if (e.getParameter().equals("userNotExists")) {
            log.error("Пользователь с указанным ID ещё не зарегистрирован в системе. " +
                    "Сначала необходимо его создать (метод POST).");
            return new ErrorResponse("Пользователь с указанным ID ещё не зарегистрирован в системе. " +
                    "Сначала необходимо его создать (метод POST).");
        }
        if (e.getParameter().equals("filmNotExists")) {
            log.error("Фильм с указанным ID ещё не зарегистрирован в системе. Его нельзя обновить. " +
                    "Его нужно сначала создать (метод POST).");
            return new ErrorResponse("Фильм с указанным ID ещё не зарегистрирован в системе. Его нельзя обновить. " +
                    "Его нужно сначала создать (метод POST).");
        }
        if (e.getParameter().equals("id=friend_id")) {
            log.error("Пользователь не может дружить сам с собой.");
            return new ErrorResponse("Пользователь не может дружить сам с собой.");
        }
        if (e.getParameter().equals("Row exists")) {
            log.error("Такая запись уже существует.");
            return new ErrorResponse("Такая запись уже существует.");
        }
        if (e.getParameter().equals("Row doesn't exist")) {
            log.error("Такая запись не существует.");
            return new ErrorResponse("Такая запись не существует.");
        }
        log.error("Неверно указан " + e.getParameter());
        return new ErrorResponse(
                String.format("Неверно указан %s.", e.getParameter()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIdExistsException(final IdExistsException e) {
        if (e.getParameter().equals("userIdExists")) {
            log.error("Пользователь уже зарегистрирован в системе. " +
                    "Его нельзя создать. Можно только обновить данные (метод PUT).");
            return new ErrorResponse(
                    "Пользователь уже зарегистрирован в системе. " +
                            "Его нельзя создать. Можно только обновить данные (метод PUT).");
        }
        if (e.getParameter().equals("filmIdExists")) {
            log.error("Фильм уже зарегистрирован в системе. Его нельзя создать. " +
                    "Можно только обновить данные (метод PUT).");
            return new ErrorResponse("Фильм уже зарегистрирован в системе. Его нельзя создать. " +
                    "Можно только обновить данные (метод PUT).");
        }
        log.error("Неверно указан " + e.getParameter());
        return new ErrorResponse(
                String.format("Неверно указан %s.", e.getParameter()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArgNotPositiveException(final ArgNotPositiveException e) {
        log.error("Параметр запроса {} не может отрицательным или равным нулю.", e.getParameter());
        return new ErrorResponse(
                String.format("Параметр запроса %s не может отрицательным или равным нулю.", e.getParameter()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        String message = "Unknown error";
        if (e.getParameter().equals("login")) {
            message = "Логин не может содержать пробелы.";
            log.error(message);
        }
        if (e.getParameter().equals("releaseDate")) {
            message = "Дата релиза не может быть ранее 28 декабря 1895 г.";
            log.error(message);
        }
        if (e.getParameter().equals("genre_id")) {
            message = "Необходимо указать корректный ID жанра фильма.";
            log.error(message);
        }
        if (e.getParameter().equals("mpa_rating_id")) {
            message = "Необходимо указать корректный ID рейтинга MPA.";
            log.error(message);
        }
        if (e.getParameter().equals("FILM: film_name + releaseDate")) {
            message = "Фильм с таким названием и датой выхода уже есть в базе";
            log.error(message);
        }
        if (e.getParameter().equals("email")) {
            message = "Пользователь с такой электронной почтой уже есть в базе";
            log.error(message);
        }
        return new ErrorResponse(
                String.format("Ошибка валидации в следующих полях: [" + e.getParameter() + "]: " + message));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        log.error("Ошибка валидации в следующих полях: {}", errors);
        return new ErrorResponse(
                String.format("Ошибка валидации в следующих полях: %s", errors));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }
}
