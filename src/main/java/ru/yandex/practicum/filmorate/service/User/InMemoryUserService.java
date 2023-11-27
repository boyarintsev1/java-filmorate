package ru.yandex.practicum.filmorate.service.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

/**
 * класс для работы с данными о User в памяти
 */
@Service
@Slf4j
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public InMemoryUserService(@Qualifier(value = "inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Map<Long, User> getUsers() {
        return userStorage.getUsers();
    }

    /**
     * метод получения списка всех пользователей
     */
    @Override
    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    /**
     * метод получения данных о пользователе по его ID
     */
    @Override
    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    /**
     * метод создания нового пользователя
     */
    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    /**
     * метод обновления данных о пользователе
     */
    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    /**
     * метод добавления пользователя в список друзей
     */
    @Override
    public User addNewFriend(long id, long friendId) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new IncorrectIdException("FriendID");
        }
        log.info("Стали друзьями пользователи c ID=" + id + " и с ID=" + friendId);
        userStorage.getUsers().get(id).getFriends().add(friendId);
        userStorage.getUsers().get(friendId).getFriends().add(id);
        return userStorage.getUsers().get(id);
    }

    /**
     * метод удаления пользователя из друзей
     */
    @Override
    public User deleteFriend(long id, long friendId) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new IncorrectIdException("FriendID");
        }
        log.info("Более не являются друзьями пользователи c ID=" + id + " и с ID=" + friendId);
        userStorage.getUsers().get(id).getFriends().remove(friendId);
        userStorage.getUsers().get(friendId).getFriends().remove(id);
        return userStorage.getUsers().get(id);
    }

    /**
     * метод получения списка друзей указанного пользователя
     */
    @Override
    public Set<User> findUserFriends(Long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        Comparator<User> userFriendsSort = Comparator.comparingLong(User::getId);
        Set<User> setOfFriends = new TreeSet<>(userFriendsSort);
        for (Long i : userStorage.getUsers().get(id).getFriends()) {
            setOfFriends.add(userStorage.getUsers().get(i));
        }
        log.info("Получен список друзей пользователя c ID=" + id);
        return setOfFriends;
    }

    /**
     * метод получения списка общих друзей двух пользователей
     */
    @Override
    public Set<User> findCommonFriends(long id, long otherId) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        if (!userStorage.getUsers().containsKey(otherId)) {
            throw new IncorrectIdException("FriendID");
        }
        Comparator<User> userFriendsSort = Comparator.comparingLong(User::getId);
        final Set<User> commonFriends = new TreeSet<>(userFriendsSort);
        for (Long i : userStorage.getUsers().get(id).getFriends()) {
            for (Long k : userStorage.getUsers().get(otherId).getFriends()) {
                if (k.equals(i)) {
                    commonFriends.add(userStorage.getUsers().get(k));
                }
            }
        }
        log.info("Получен список общих друзей пользователей c ID=" + id + " и ID=" + otherId);
        return commonFriends;
    }
}

