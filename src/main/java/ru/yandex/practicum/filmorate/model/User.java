package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    @NotNull
    @NotEmpty
    @NotBlank
    private final String login;
    private String name;
    private int id;
    @NotNull
    @NotEmpty
    @Email(message = "Email должен быть корректным адресом электронной почты")
    @NotBlank
    private final String email;
    @NotNull
    @Past(message = "Дата рождения не может быть из будущего")
    private final LocalDate birthday;

    public void setId(int id) {
        this.id = id;
    }
}


