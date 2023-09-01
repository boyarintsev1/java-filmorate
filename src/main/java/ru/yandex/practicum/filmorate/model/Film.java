package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;

@Data
public class Film {
    @EqualsAndHashCode.Exclude
    private int id;
    @NotNull
    @NotEmpty
    @NotBlank
    private final String name;
    @NotNull
    @EqualsAndHashCode.Exclude
    @Size(max = 200, message = "Размер описания не может превышать 200 символов")
    private final String description;
    @NotNull
    @Past()
    private final LocalDate releaseDate;
    @NotNull
    @EqualsAndHashCode.Exclude
    @Positive(message = "Длительность фильма должна быть больше нуля")
    private final int duration;
    @EqualsAndHashCode.Exclude
    private int rate;

    public void setId(int id) {
        this.id = id;
    }
}
