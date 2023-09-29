package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.IdExistsException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    final Map<Integer, User> users = new HashMap<>();
    int id = 0;

    public Map<Integer, User> getUsers() {
        return users;
    }

    public Collection<User> findAllUsers() {                      // получение всех пользователей
        return users.values();
    }

    @Override
    public User findUserById(int id) {                            // получение пользователя по ID
        if (!users.containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        return users.get(id);
    }

    @Override
    public User createUser(@Valid @RequestBody User user) {            //создание нового пользователя
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

    @Override
    public User updateUser(@Valid @RequestBody User user) {                //обновление данных пользователя
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

