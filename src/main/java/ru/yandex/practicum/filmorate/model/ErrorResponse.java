package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {

    private final String error;     // название ошибки

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
