package ru.yandex.practicum.filmorate.service.User;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * интерфейс для работы с данными о User
 */
public interface UserService {
    Map<Long, User> getUsers();

    /**
     * метод получения списка всех пользователей
     */
    List<User> findAllUsers();

    /**
     * метод получения данных о пользователе по его ID
     */
    User findUserById(Long id);

    /**
     * метод создания нового пользователя
     */
    User createUser(User user);

    /**
     * метод обновления данных о пользователе
     */
    User updateUser(User user);

    /**
     * метод добавления пользователя в список друзей
     */
    User addNewFriend(long id, long friendId);

    /**
     * метод удаления пользователя из друзей
     */
    User deleteFriend(long id, long friendId);

    /**
     * метод получения списка друзей указанного пользователя
     */
    Set<User> findUserFriends(Long id);

    /**
     * метод получения списка общих друзей двух пользователей
     */
    Set<User> findCommonFriends(long id, long otherId);
}
