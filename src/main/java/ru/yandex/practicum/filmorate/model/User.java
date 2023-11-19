package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@JsonPropertyOrder({"id", "name", "login", "email", "birthday", "friends"})
public class User {
    @NotNull
    @EqualsAndHashCode.Exclude
    @NotEmpty
    @NotBlank
    private final String login;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotNull
    @NotEmpty
    @Email(message = "Email должен быть корректным адресом электронной почты")
    @NotBlank
    private final String email;
    @NotNull
    @EqualsAndHashCode.Exclude
    @Past(message = "Дата рождения не может быть из будущего")
    private final LocalDate birthday;
    @EqualsAndHashCode.Exclude
    private Set<Long> friends;

    @JsonCreator
    public User(String login, String name, String email,
                LocalDate birthday, Set<Long> friends) {  //, Set<Long> unconfirmedFriends
        this.name = name;
        this.login = login;
        this.email = email;
        this.birthday = birthday;
        this.friends = friends;
    }

    public User(Long id, String login, String name, String email,
                LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.email = email;
        this.birthday = birthday;
        this.friends = friends;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public Set<Long> getFriends() {
        return friends;
    }

    public void setFriends(Set<Long> friends) {
        this.friends = friends;
    }
}


