package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class Film {
    private int id;
    @NotNull
    @NotEmpty
    @NotBlank
    private final String name;
    @NotNull
    @Size(max = 200, message = "Размер описания не может превышать 200 символов")
    private final String description;
    @NotNull
    @Past()
    private final LocalDate releaseDate;
    @NotNull
    @Positive(message = "Длительность фильма должна быть больше нуля")
    private final int duration;
    private int rate;

    public void setId(int id) {
        this.id = id;
    }
}
