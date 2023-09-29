package ru.yandex.practicum.filmorate.exception;

public class IncorrectIdException extends RuntimeException {
    private final String parameter;

    public IncorrectIdException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }

}

