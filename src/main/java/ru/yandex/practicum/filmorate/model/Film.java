package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film  {
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
    @Past(message = "Дата релиза не может быть датой из будущего")
    private final LocalDate releaseDate;
    @NotNull
    @EqualsAndHashCode.Exclude
    @Positive(message = "Длительность фильма должна быть больше нуля")
    private final int duration;
    @EqualsAndHashCode.Exclude
    private int rate;
    private Set<Integer> likes = new HashSet<>();

    public void setId(int id) {
        this.id = id;
    }

}
