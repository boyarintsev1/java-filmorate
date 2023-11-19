package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonPropertyOrder({"id", "name"})
public class Mpa {
    private int id;
    @NotNull
    @NotEmpty
    @NotBlank
    private final String name;

    public static final String[] mpa_rating_names = {"G", "PG", "PG-13", "R", "NC-17"};
    // G — у фильма нет возрастных ограничений,
    // PG — детям рекомендуется смотреть фильм с родителями,
    // PG-13 — детям до 13 лет просмотр не желателен,
    // R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
    // NC-17 — лицам до 18 лет просмотр запрещён.

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
}


