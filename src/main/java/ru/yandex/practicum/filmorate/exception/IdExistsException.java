package ru.yandex.practicum.filmorate.exception;

public class IdExistsException extends RuntimeException {
    private final String parameter;

    public IdExistsException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
