package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseBody
@Slf4j
@RequestMapping("/users")

public class UserController {
    static final Map<Integer, User> users = new HashMap<>();
    static int id = 0;

    @GetMapping
    public static Collection<User> findAllUsers() {              // получение всех пользователей
        return users.values();
    }

    @PostMapping
    public static User createUser(@Valid @RequestBody User user) {            //создание нового пользователя
        for (User i : users.values()) {
            if (user.getEmail().equals(i.getEmail())) {
                log.error("Пользователь с электронной почтой " +
                        user.getEmail() + " уже зарегистрирован в системе. " +
                        "Его нельзя создать. Можно только обновить данные (метод PUT).");
                throw new ValidationException("Пользователь с электронной почтой " +
                        user.getEmail() + " уже зарегистрирован в системе. " +
                        "Его нельзя создать. Можно только обновить данные (метод PUT).");
            }
        }
        if (containsSpace(user.getLogin())) {
            log.error("Логин не может содержать пробелы.");
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        id = id + 1;
        user.setId(id);
        log.info("Будет сохранен объект: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public static User updateUser(@Valid @RequestBody User user) {                //обновление данных пользователя
        for (User i : users.values()) {
            if (user.getId() != i.getId()) {
                log.error("Пользователь с электронной почтой " +
                        user.getEmail() + " ещё не зарегистрирован в системе. " +
                        "Сначала необходимо его создать (метод POST).");
                throw new ValidationException("Пользователь с электронной почтой " +
                        user.getEmail() + " ещё не зарегистрирован в системе. " +
                        "Сначала необходимо его создать (метод POST).");
            } else {
                user.setId(i.getId());
                log.info("Будет обновлен объект: {}", user);
                users.put(user.getId(), user);
            }
        }
        if (containsSpace(user.getLogin())) {
            log.error("Логин не может содержать пробелы.");
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return user;
    }

    public static boolean containsSpace(String input) {    //метод определения наличия пробелов в поле класса
        if (!input.isEmpty()) {
            for (int i = 0; i < input.length(); i++) {
                if (Character.isWhitespace(input.charAt(i)) || Character.isSpaceChar(input.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }
}

