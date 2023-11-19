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

    public Map<Long, User> getUsers() {
        return userStorage.getUsers();
    }

    public List<User> findAllUsers() {              // получение всех пользователей
        return userStorage.findAllUsers();
    }

    public User findUserById(Long id) {                  // получение пользователя по Id
        return userStorage.findUserById(id);
    }

    public User createUser(User user) {                 //создание нового пользователя
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {                //обновление данных пользователя
        return userStorage.updateUser(user);
    }

    public User addNewFriend(long id, long friendId) {                         //метод добавления нового друга
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

    public User deleteFriend(long id, long friendId) {                          //метод удаления друга из друзей
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

    public Set<User> findUserFriends(Long id) {                               //метод поиска друзей пользователя
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

    public Set<User> findCommonFriends(long id, long otherId) {           //метод поиска общих друзей двух пользователей
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

