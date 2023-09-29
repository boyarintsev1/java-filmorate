package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @NotNull
    @EqualsAndHashCode.Exclude
    @NotEmpty
    @NotBlank
    private final String login;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private int id;
    @NotNull
    @NotEmpty
    @Email(message = "Email должен быть корректным адресом электронной почты")
    @NotBlank
    private final String email;
    @NotNull
    @EqualsAndHashCode.Exclude
    @Past(message = "Дата рождения не может быть из будущего")
    private final LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public void setId(int id) {
        this.id = id;
    }

}


