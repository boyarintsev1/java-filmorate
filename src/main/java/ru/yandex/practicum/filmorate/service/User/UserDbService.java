package ru.yandex.practicum.filmorate.service.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

/**
 * класс для работы с данными о User в БД H2
 */
@Service
@Slf4j
public class UserDbService implements UserService {
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_IN_FRIENDSHIP_QUERY =
            "insert into FRIENDSHIP (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_IN_FRIENDSHIP_QUERY =
            "delete from FRIENDSHIP  WHERE user_id= ? AND friend_id=?";

    @Autowired
    public UserDbService(@Qualifier(value = "userDbStorage") UserStorage userStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getUsers() {
        return null;
    }

    @Override
    public List<User> findAllUsers() {              // получение всех пользователей
        return userStorage.findAllUsers();
    }

    @Override
    public User findUserById(Long id) {                  // получение пользователя по Id
        return userStorage.findUserById(id);
    }

    @Override
    public User createUser(User user) {                 //создание нового пользователя
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {                //обновление данных пользователя
        return userStorage.updateUser(user);
    }

    @Override
    public User addNewFriend(long id, long friendId) {                         //метод добавления нового друга
        String s = "select count(*) from USERS where id = '" + id + "'";
        if ((jdbcTemplate.queryForObject(s, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(s, Integer.class) == null)) {
            throw new IncorrectIdException("UserID");
        }
        String f = "select count(*) from USERS where id = '" + friendId + "'";
        if ((jdbcTemplate.queryForObject(f, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(f, Integer.class) == null)) {
            throw new IncorrectIdException("FriendID");
        }
        if ((id == friendId)) {
            throw new IncorrectIdException("id=friend_id");
        }
        try {
            jdbcTemplate.update(INSERT_IN_FRIENDSHIP_QUERY, id, friendId);
        } catch (Exception e) {
            throw new IncorrectIdException("Row exists");
        }
        log.info("Будет обновлен объект: {}", findUserById(id));
        log.info("Стали друзьями пользователи c ID=" + id + " и с ID=" + friendId);
        return findUserById(id);
    }

    @Override
    public User deleteFriend(long id, long friendId) {                          //метод удаления друга из друзей
        String s = "select count(*) from USERS where id = '" + id + "'";
        if ((jdbcTemplate.queryForObject(s, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(s, Integer.class) == null)) {
            throw new IncorrectIdException("UserID");
        }
        String f = "select count(*) from USERS where id = '" + friendId + "'";
        if ((jdbcTemplate.queryForObject(f, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(f, Integer.class) == null)) {
            throw new IncorrectIdException("FriendID");
        }
        if ((id == friendId)) {
            throw new IncorrectIdException("id=friend_id");
        }
        try {
            jdbcTemplate.update(DELETE_IN_FRIENDSHIP_QUERY, id, friendId);

        } catch (Exception e) {
            throw new IncorrectIdException("Row doesn't exist");
        }
        log.info("Будет обновлен объект: {}", findUserById(id));
        log.info("Более не являются друзьями пользователи c ID=" + id + " и с ID=" + friendId);
        return findUserById(id);
    }

    @Override
    public Set<User> findUserFriends(Long id) {                               //метод поиска друзей пользователя
        String s = "select count(*) from USERS where id = '" + id + "'";
        if ((jdbcTemplate.queryForObject(s, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(s, Integer.class) == null)) {
            throw new IncorrectIdException("UserID");
        }
        Comparator<User> userFriendsSort = Comparator.comparingLong(User::getId);
        Set<User> setOfFriends = new TreeSet<>(userFriendsSort);
        for (Long i : findUserById(id).getFriends()) {
            setOfFriends.add(findUserById(i));
        }
        log.info("Получен список друзей пользователя c ID=" + id);
        return setOfFriends;
    }

    @Override
    public Set<User> findCommonFriends(long id, long otherId) {           //метод поиска общих друзей двух пользователей
        String s = "select count(*) from USERS where id = '" + id + "'";
        if ((jdbcTemplate.queryForObject(s, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(s, Integer.class) == null)) {
            throw new IncorrectIdException("UserID");
        }
        String f = "select count(*) from USERS where id = '" + otherId + "'";
        if ((jdbcTemplate.queryForObject(f, Integer.class) == 0) ||
                (jdbcTemplate.queryForObject(f, Integer.class) == null)) {
            throw new IncorrectIdException("FriendID");
        }
        Comparator<User> userFriendsSort = Comparator.comparingLong(User::getId);
        final Set<User> commonFriends = new TreeSet<>(userFriendsSort);
        for (Long i : findUserById(id).getFriends()) {
            for (Long k : findUserById(otherId).getFriends()) {
                if (k.equals(i)) {
                    commonFriends.add(findUserById(k));
                }
            }
        }
        log.info("Получен список общих друзей пользователей c ID=" + id + " и ID=" + otherId);
        return commonFriends;
    }
}
