package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends RuntimeException {
    private final String parameter;

    public ValidationException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
