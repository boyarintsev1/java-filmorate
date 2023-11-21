package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.IdExistsException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * класс хранения и обработки данных о пользователях User в памяти
 */
@Component
@Qualifier("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    final Map<Long, User> users = new HashMap<>();
    long id = 0;

    /**
     * метод получения данных о всех пользователях в виде HashMap
     */
    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    /**
     * метод получения списка всех пользователей
     */
    public List<User> findAllUsers() {                      // получение всех пользователей
        return new ArrayList<>(users.values());
    }

    /**
     * метод получения данных о пользователе по его ID
     */
    @Override
    public User findUserById(long id) {
        if (!users.containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        return users.get(id);
    }

    /**
     * метод создания нового пользователя
     */
    @Override
    public User createUser(@Valid @RequestBody User user) {
        if (users.containsValue(user)) {
            throw new IdExistsException("userIdExists");
        }
        if (containsSpace(user.getLogin())) {
            throw new ValidationException("login");
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

    /**
     * метод обновления данных о пользователе
     */
    @Override
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new IncorrectIdException("userNotExists");
        } else {
            users.put(user.getId(), user);
        }
        if (containsSpace(user.getLogin())) {
            throw new ValidationException("login");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Обновлен объект: {}", user);
        return user;
    }

    /**
     * метод определения наличия пробелов в поле класса
     */
    public boolean containsSpace(String input) {
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

