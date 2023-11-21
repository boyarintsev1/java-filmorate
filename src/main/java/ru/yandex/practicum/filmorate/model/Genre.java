package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonPropertyOrder({"id", "name"})
public class Genre {
    private long id;
    @NotNull
    @NotEmpty
    @NotBlank
    private final String name;
    public static final String[] GENRES_NAMES = {"Комедия", "Драма", "Мультфильм", "Триллер",
            "Документальный", "Боевик"};

    public Genre(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
}



