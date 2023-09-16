package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Service
@Slf4j
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = (InMemoryUserStorage) inMemoryUserStorage;
    }

    public User addNewFriend(int id, int friendId) {                         //метод добавления нового друга
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(friendId)) {
            throw new IncorrectIdException("FriendID");
        }
        log.info("Стали друзьями пользователи c ID=" + id + " и с ID=" + friendId);
        inMemoryUserStorage.getUsers().get(id).getFriends().add(friendId);
        inMemoryUserStorage.getUsers().get(friendId).getFriends().add(id);
        return inMemoryUserStorage.getUsers().get(id);
    }

    public User deleteFriend(int id, int friendId) {                          //метод удаления друга из друзей
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(friendId)) {
            throw new IncorrectIdException("FriendID");
        }
        log.info("Более не являются друзьями пользователи c ID=" + id + " и с ID=" + friendId);
        inMemoryUserStorage.getUsers().get(id).getFriends().remove(friendId);
        inMemoryUserStorage.getUsers().get(friendId).getFriends().remove(id);
        return inMemoryUserStorage.getUsers().get(id);
    }

    public Set<User> findUserFriends(int id) {                               //метод поиска друзей пользователя
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        Comparator<User> userFriendsSort = Comparator.comparingInt(User::getId);
        Set<User> setOfFriends = new TreeSet<>(userFriendsSort);
        for (int i : inMemoryUserStorage.getUsers().get(id).getFriends()) {
            setOfFriends.add(inMemoryUserStorage.getUsers().get(i));
        }
        log.info("Получен список друзей пользователя c ID=" + id);
        return setOfFriends;
    }

    public Set<User> findCommonFriends(int id, int otherId) {           //метод поиска общих друзей двух пользователей
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            throw new IncorrectIdException("UserID");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(otherId)) {
            throw new IncorrectIdException("FriendID");
        }
        Comparator<User> userFriendsSort = Comparator.comparingInt(User::getId);
        final Set<User> commonFriends = new TreeSet<>(userFriendsSort);
        for (Integer i : inMemoryUserStorage.getUsers().get(id).getFriends()) {
            for (Integer k : inMemoryUserStorage.getUsers().get(otherId).getFriends()) {
                if (k.equals(i)) {
                    commonFriends.add(inMemoryUserStorage.getUsers().get(k));
                }
            }
        }
        log.info("Получен список общих друзей пользователей c ID=" + id + " и ID=" + otherId);
        return commonFriends;
    }
}
