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
    final Map<Integer, User> users = new HashMap<>();
    int id = 0;

    @GetMapping
    public Collection<User> findAllUsers() {              // получение всех пользователей
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {            //создание нового пользователя
        if (users.containsValue(user)) {
            log.error("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован в системе. " +
                    "Его нельзя создать. Можно только обновить данные (метод PUT).");
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован в системе. " +
                    "Его нельзя создать. Можно только обновить данные (метод PUT).");
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
    public User updateUser(@Valid @RequestBody User user) {                //обновление данных пользователя
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с ID <" + user.getId() + "> с электронной почтой " +
                    user.getEmail() + " ещё не зарегистрирован в системе. " +
                    "Сначала необходимо его создать (метод POST).");
            throw new ValidationException("Пользователь с ID <" + user.getId() + "> с электронной почтой "  +
                    user.getEmail() + " ещё не зарегистрирован в системе. " +
                    "Сначала необходимо его создать (метод POST).");
        } else {
                log.info("Будет обновлен объект: {}", user);
                users.put(user.getId(), user);
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

    public boolean containsSpace(String input) {    //метод определения наличия пробелов в поле класса
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

