package ru.yandex.practicum.filmorate.exception;

public class ArgNotPositiveException extends RuntimeException {
    private final String parameter;

    public ArgNotPositiveException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}

