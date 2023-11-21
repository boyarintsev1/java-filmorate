package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

/**
 * интерфейс хранения данных о пользователях User
 */
public interface UserStorage {
    /**
     * метод получения данных о всех пользователях в виде HashMap
     */
    Map<Long, User> getUsers();

    /**
     * метод получения списка всех пользователей
     */
    List<User> findAllUsers();

    /**
     * метод получения данных о пользователе по его ID
     */
    User findUserById(long id);

    /**
     * метод создания нового пользователя
     */
    User createUser(User user);

    /**
     * метод обновления данных о пользователе
     */
    User updateUser(User user);
}
